/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ge2012;

/**
 *
 * @author dcentore
 */
public class GE2012
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        System.out.println("Hello folks");
        System.out.println("Okay, so what I'm going to show you how to do is commit.");
        System.out.println("Committing in mercurial (hg) is NOT like svn. It only keeps it on your local hard drive. Watch.");
        
        System.out.println("It went away! Okay, we just reverted a code change. Now let's make a change that I decide is ready for the main repo.");
        
        foo();
        
        System.out.println("Okay, first lets commit this to mine.");

        // Now, for the sake of netbeans (ew) let's show you how to do the same stuff there.
        
        bar();
    }
    
    private static void bar()
    {
		// Looks like I made a change :-o
		
    	// But NetBeans added comments! Gasp!
    	// So, now first I commit then I'll push.
	}

	static void foo()
    {
    	
    }
}
