# Installation #
  * The project website is here: http://code.google.com/p/amity-ge-2012/
  * The repository url can be found on the website under Source>Checkout. It will look something like: https://drdanielfc@code.google.com/p/amity-ge-2012/ (but don't use that, you don't have Dr. Frank's password).
  * When I refer to your password, I am not talking about the password you use to log into your Google account. I am referring to the one you can find here: https://code.google.com/hosting/settings
  * Mercurial is the version control system we will use. It lets us work as a group on the code. It is similar to SVN.
  * hg is Mercurial.

## Eclipse ##
  * Install MercurialEclipse
    * Navigate to Help>Install New Software....
    * Under “work with” put in the address of the MercurialEclipse update site. You can find the update site at http://javaforge.com/project/HGE#download . (Note: This is not the site - you have to open that site in a browser to find the update site) and press enter. (it might take a minute to load)
    * Linux: Tick off MercurialEclipse>MercurialEclipse (don't grab the Windows libraries unless you're using Windows)
    * Windows: Tick off the entire MercurialEclipse tree (including Windows libraries)
    * Next>Next>Accept>Finish>OK (allow unsigned content)>Restart Now
  * Import the Project
    * In white area in the Package Explorer on the left,
      * Right Click > Import > Mercurial > Clone Existing Mercurial Repository>Next
      * Input the following info:
      * URL: The repository URL
      * Username: Your gmail email address (ie drdanielfc@gmail.com)
      * Password: Your password
      * Checkout as a project(s) in the workspace: Make sure this is ticked
      * Next>Next>Finish

## NetBeans ##
  * Team>Mercurial>Clone Other....
  * Repository URL: The Repository URL
  * User: Your gmail email address (ie drdanielfc@gmail.com)
  * Password: Your password
  * Next
  * Make yourself a master password if necessary or put in your current one
  * Next
  * Scan for NetBeans Projects after Clone: Make sure this is Ticked
  * Finish
  * Open Project (this is important!)

# Use Instructions #

## Information for all Platforms ##
  * Revision: A save point of all the code at a specific point in time.
    * In SVN they were numbered (1, 2, 3....)
    * In HG they have names like 3df19fc50e26. You identify them by what you wrote for the commit log (important!)
    * Commit: This saves all your code on your computer so you can go back to that revision at any time. In SVN this uploaded it to Google Code. In HG, IT DOES NOT.
    * Push: Sends all your changes to the main server that we all work off of
    * Pull: Pulls in any new code that other people wrote
    * Merge: Takes two revisions and (tries to) mash them together. Sometimes you will have to fix it. (But hg can fix them better than svn could.)


# See Also #
  * Transition from svn: http://hginit.com/00.html
  * In general: http://hginit.com/index.html
  * Std tutorial: http://mercurial.selenic.com/wiki/Tutorial -- does it bother anyone else that it's mercurial but the selenic? I mean, Se and Hg are entirely distinct! They're even in different groups! And in different periods! And not even selenic periods!