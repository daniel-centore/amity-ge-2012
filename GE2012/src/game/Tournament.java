package game;
import hex.HexMove;
import hex.HexState;

import java.io.File;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Date;

public class Tournament {
	public static Params tournamentParams;
	public static Params gameParams;

	public static Client [] clients;
	public static ArrayList<GameThread> threads;

	private static class Game {
		public int h, a;
		public int gameNum;
		public Game(int h, int a, int n)
		{
			this.h = h;
			this.a = a;
			this.gameNum = n;
		}
	}

	private static String printableName(String name)
	{
		if (name.length() <= 13)
			return name;
		else
			return name.substring(0, 13);
	}
	private static int randInt(int lo, int hi)
	{
		int delta = hi - lo + 1;
		return (int)(Math.random() * delta) + lo;
	}

	private static void shuffle(ArrayList<Game> list)
	{
		int sz = list.size();
		for (int i=0; i<sz; i++) {
			int newSpot = randInt(i, sz-1);

			Game old = list.get(i);
			Game neww = list.get(newSpot);
			list.set(i, neww);
			list.set(newSpot, old);
		}
	}
	public static boolean randomTeam(int t)
	{ return !clients[t].deterministic; }
	public static boolean stochasticGame(int h, int a)
	{ return randomTeam(h) || randomTeam(a); }
	public static boolean deterministicGame(int h, int a)
	{ return !stochasticGame(h, a); }
	public static void launchTournament(GameState st, GameMove move)
	{
		File dumpsDir = new File("Dumps");
		dumpsDir.mkdir();
		Date date = new Date();
		String dumpDir = "Dumps/" + date.toString() + "/";
		dumpDir = dumpDir.replace(':', ';');
		File file = new File(dumpDir);
		file.mkdir();
		GameThread.dir = dumpDir;
		int NUM_CLIENTS = tournamentParams.integer("NUMCLIENTS");
		int PORT = tournamentParams.integer("PORT");
		int NUM_GAMES = tournamentParams.integer("NUMGAMES");

		int INIT_LIMIT = gameParams.integer("INITTIME");
		int MOVE_LIMIT = gameParams.integer("MOVETIME");
		int GAME_LIMIT = gameParams.integer("GAMETIME");
		int FINAL_PLAY_LIMIT = gameParams.integer("MAXMOVETIME");
		int MAX_WARNINGS = gameParams.integer("NUMWARNINGS");

		int p1, p2;
		int i;
		int headToHeadSummary[][][] = new int [NUM_CLIENTS][NUM_CLIENTS][3];
		int homeSummary[][] = new int [NUM_CLIENTS][3];
		int awaySummary[][] = new int [NUM_CLIENTS][3];
		int totalSummary[][] = new int [NUM_CLIENTS][3];
		ArrayList<Game> schedule = new ArrayList<Game>();
		threads = new ArrayList<GameThread>();

		try {
			ServerSocket socket = new ServerSocket(PORT);
			clients = new Client [NUM_CLIENTS];
			for (i=0; i<NUM_CLIENTS; i++) {
				clients[i] = new Client(socket, INIT_LIMIT, MOVE_LIMIT, FINAL_PLAY_LIMIT,
						GAME_LIMIT, MAX_WARNINGS);
				if (clients[i].name.toUpperCase().contains("HUMAN")) {
					clients[i].moveLimit = clients[i].finalPlayLimit = gameParams.integer("HUMANTIME");  
					clients[i].gameTimeLimit = 10000;  
				}
				File subdir = new File(dumpDir + "/" + clients[i].name);
				subdir.mkdir();
				System.out.printf("%s has joined%n", clients[i].name);
			}

			for (p1=0; p1<NUM_CLIENTS; p1++) {
				for (p2=0; p2<NUM_CLIENTS; p2++) {
					if (p1 == p2) continue;
					int numToPlay;
					if (stochasticGame(p1, p2)) {
						numToPlay = NUM_GAMES;
					} else {
						numToPlay = 1;
					}
					for (i=0; i<numToPlay; i++) {
						schedule.add(new Game(p1, p2, i));
					}
				}
			}
			shuffle(schedule);

			int remain = schedule.size();

			while (remain > 0) {
				Thread.sleep(500);
				boolean cont = true;
				while (cont) {
					cont = false;
					for (i=0; i<schedule.size(); i++) {
						Game g = schedule.get(i);
						p1 = g.h;
						p2 = g.a;
						int consecWins = Tournament.tournamentParams.integer("CONSECWINS");
						if ((headToHeadSummary[p1][p2][0] >= consecWins &&
								headToHeadSummary[p1][p2][1] + headToHeadSummary[p1][p2][2] == 0) ||
								(headToHeadSummary[p1][p2][0] + headToHeadSummary[p1][p2][2] == 0 && 
								headToHeadSummary[p1][p2][1] >= consecWins)) {
							schedule.remove(i);
							remain--;
							if (headToHeadSummary[p1][p2][0] == 0) {
								headToHeadSummary[p1][p2][1]++; 
								homeSummary[p1][1]++;
								awaySummary[p2][0]++;
								totalSummary[p1][1]++;
								totalSummary[p2][0]++;
							} else {
								headToHeadSummary[p1][p2][0]++; 
								homeSummary[p1][0]++;
								awaySummary[p2][1]++;
								totalSummary[p1][0]++;
								totalSummary[p2][1]++;
							}
						} else if (!clients[g.h].busy && !clients[g.a].busy) {
							GameThread game = new GameThread(clients[g.h], g.h, clients[g.a], g.a,
									g.gameNum, move.clone(), st.clone());
							game.start();
							clients[g.h].busy = clients[g.a].busy = true;
							threads.add(game);
							schedule.remove(i);
							System.out.printf("%s-%s (%d)%n", clients[g.h].name, clients[g.a].name, remain);
							cont = true;
						}
					}
				}
				i = 0;
				while (i<threads.size()) {
					GameThread t = threads.get(i);
					if (t.isAlive()) {
						i++;
					} else {
						p1 = t.homeID;
						p2 = t.awayID;
						clients[p1].busy = clients[p2].busy = false;
						GameState.Status outcome = t.result;
						threads.remove(i);
						remain--;

						int cnt = stochasticGame(p1, p2) ? 1 : NUM_GAMES;
						if (outcome == GameState.Status.HOME_WIN) {
							System.out.printf("home (%s) won%n", clients[p1].name);
							headToHeadSummary[p1][p2][0] += cnt;
							awaySummary[p2][1] += cnt;
							homeSummary[p1][0] += cnt;
							totalSummary[p1][0] += cnt;
							totalSummary[p2][1] += cnt;
						} else if (outcome == GameState.Status.AWAY_WIN) {
							System.out.printf("away (%s) won%n", clients[p2].name);
							headToHeadSummary[p1][p2][1] += cnt;
							awaySummary[p2][0] += cnt;
							homeSummary[p1][1] += cnt;
							totalSummary[p1][1] += cnt;
							totalSummary[p2][0] += cnt;
						} else if (outcome == GameState.Status.DRAW) {
							System.out.println(" draw\n");
							headToHeadSummary[p1][p2][2] += cnt;
							awaySummary[p2][2] += cnt;
							homeSummary[p1][2] += cnt;
							totalSummary[p1][2] += cnt;
							totalSummary[p2][2] += cnt;
						} else {
							System.err.println("Error with game outcome");
						}
					}
				}
			}

			for (int j=0; j<NUM_CLIENTS; j++) {
				clients[j].simpleMsg("DONE");
			}
			System.out.printf("%13s ", "");
			for (p1=0; p1<NUM_CLIENTS; p1++) {
				System.out.printf("%13s ", printableName(clients[p1].name));
			}
			System.out.println();

			for (p1=0; p1<NUM_CLIENTS; p1++) {
				System.out.printf("%13s ", printableName(clients[p1].name));
				for (p2=0; p2<NUM_CLIENTS; p2++) {
					if (p1 == p2) {
						System.out.printf("%13s ", "");
					} else {
						System.out.printf("(%3d %3d %3d) ", headToHeadSummary[p1][p2][0],
								headToHeadSummary[p1][p2][1],
								headToHeadSummary[p1][p2][2]);
					}
				}
				System.out.printf("(%3d %3d %3d)%n", homeSummary[p1][0],
						homeSummary[p1][1],
						homeSummary[p1][2]);
			}
		}
		catch (Exception e) {
			System.out.println("Server problem" + e);
		}

		System.out.printf("%13s ", "");
		for (p1=0; p1<NUM_CLIENTS; p1++) {
			System.out.printf("(%3d %3d %3d) ", awaySummary[p1][0],
					awaySummary[p1][1],
					awaySummary[p1][2]);
		}

		System.out.printf("%n%n");
		for (p1=0; p1<NUM_CLIENTS; p1++) {
			System.out.printf("%13s %3s (%3d %3d %3d)%n", printableName(clients[p1].name),
					clients[p1].DQd ? "DQd" : "  ",
							totalSummary[p1][0], totalSummary[p1][1],
							totalSummary[p1][2]);
		}
		System.out.println();
	}

	public static void main(String[] args)
	{
		tournamentParams = new Params("config/tournament.txt");
		System.out.println("Starting Hex tournament");
		System.out.printf("%d clients%n", tournamentParams.integer("NUMCLIENTS"));
		gameParams = new Params("config/hex.txt");
		Tournament.launchTournament(new HexState(),	new HexMove());
		System.out.println("Tournament is over");
	}
}
