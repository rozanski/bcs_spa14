#! /usr/bin/python
"""
oauth_client.py: Dropbox Python OAuth API demo client

This is the script which runs the Oauth demo client.
It checks whether the access token file exists, and presents the user with a menu of relevant options.

If the client has previously been authorised with Dropbox (ie the Dropbox token file exists),
the options allow the user to manipulate files in the Dropbox app directory using Dropbox calls
(not using filesystem I/O).

If the client has not yet been authorised with Dropbox (ie the Dropbox token file does not exist),
the options allow the user to run either the no-redirect or redirect workflows.


Global variables:
    logger: used to log error, info and debug messages
        example usage: logger = CO.logger; logger('message')
    dropbox_client: a dropbox.client.DropboxClient() which can be manipulated by the user

Workflow Methods:
    oauth_authorise_no_redirect(): run noredirect authorisation workflow (requires auth code to be entered)
    oauth_authorise_redirect(): start redirect authorisation workflow (uses HTTP web server to simulate URL schema)

Other Methods:
    oauth_access_token_file_exists(): print whether the access token file exists
    oauth_print_access_token_file(): print the access token file
    oauth_delete_access_token_file(): delete the access token file
    oauth_help(): display a help message which shows what functions can be called
    oauth_debug(): enable / disable debugging
"""

import logging

import common_oauth as CO
logger = CO.logger
import dropbox_workflow as DW
from dropbox_tools import *

def __print_workflow_prompts(prompt, additional_instructions=None):
    print """When the browser window opens:
 1. Log in to Dropbox if necessary (your user name and password are NOT visible to the client)
 2. If you have enabled two-factor authentication with Dropbox, enter the Dropbox security code
    which will have been sent to your mobile phone (this is not part of the OAuth workflow)
 3. Click 'Allow' to authorise the client with Dropbox"""
    if additional_instructions is not None: print additional_instructions
    response = raw_input(prompt+"...")

def __print_help_message(message = None):
    if message is None: message = 'Command complete'
    print ('\n%s. Type oauth_help() for help:\n' % (message))

###############################################
# AUTHORISE WITHOUT AUTOMATIC URL REDIRECTION #
###############################################
def oauth_authorise_no_redirect():
    """
    run noredirect authorisation workflow (requires auth code to be entered)

    This consists of the following steps:
    - Delete the access token file if it exists.
    - Start the workflow by calling DropboxWorkflow.no_redirect_client_start().
    - Open a browser window at the URL returned by no_redirect_client_start().
    - Prompt the user to enter the authorisation code displayed by Dropbox in this window.
    - Finish the workflow, save the access token and generate the sample files
      by calling DropboxWork.no_redirect_client_finish_and_save().

    @see https://www.dropbox.com/developers/core/docs/python#DropboxOAuth2FlowNoRedirect
    """

    # EXERCISE:
    #  - delete the access token file (use CO.AccessData.*)
    #  - start the Dropbox no-redirect workflow (use DW.*)
    #  - store the returned information in dropbox_status
# TODO ==> INSERT CODE HERE <==

    __print_workflow_prompts(
            'Press Enter to start no_redirect() authorisation',
            ' 4. Copy the provided authorisation code to the clipboard')

    # EXERCISE:
    #  - open a browser window at the URL returned by the Dropbox start step (use CO.HttpService)
    #  - prompt the user to enter the security code displayed by Dropbox
    #  - store the returned information in security_code
    #  - finish the Dropbox no-redirect workflow, passing it the security code entered by the user,
    #    and save the access token (use DW.*)
# TODO ==> INSERT CODE HERE <==

    __print_help_message('NO-REDIRECT AUTHORISATION COMPLETED SUCCESSFULLY')

############################################
# AUTHORISE WITH AUTOMATIC URL REDIRECTION #
############################################
def oauth_authorise_redirect():
    """
    start redirect authorisation workflow (uses HTTP web server to simulate URL schema)

    Start the Oauth no-redirect workflow.
    - This consists of the following steps:
    - Delete the access token file if it exists.
    - Start the workflow by calling DropboxWorkflow.redirect_client_start().
    - Open a browser window at the URL returned by redirect_client_start().
    - Wait for the access token file to be created by the HTTP server
      (which is running the finish step of the workflow).

    @see https://www.dropbox.com/developers/core/docs/python#DropboxOAuth2Flow
    """

    # EXERCISE:
    #  - delete the access token file (use CO.AccessData.*)
    #  - start the Dropbox redirect workflow (use DW.*)
    #  - store the returned information in dropbox_status
# TODO ==> INSERT CODE HERE <==

    __print_workflow_prompts('Ensure the HTTP server is running and press enter to start redirect() authorisation')

    # EXERCISE:
    #  - open a browser window at the URL returned by the Dropbox start step (use CO.HttpService)
    #  - wait for the access token to be saved (use CO.AccessData)
# TODO ==> INSERT CODE HERE <==

    __print_help_message('REDIRECT AUTHORISATION COMPLETED SUCCESSFULLY')

######################################################
# UTILITY FUNCTIONS RUN  FROM THE PYTHON INTERPRETER #
######################################################
def oauth_debug(enable):
    """enable / disable debugging"""
    global logger
    if enable:
        logger.setLevel(logging.DEBUG)
        logger.debug('Debug messages will be displayed')
    else:
        logger.setLevel(logging.INFO)
        logger.info('Debug messages will not be displayed')
    __print_help_message()

def oauth_access_token_file_exists():
    """print whether the access token file exists"""
    print CO.AccessData.access_token_file_exists()
    __print_help_message()

def oauth_print_access_token_file():
    """print the access token file"""
    CO.AccessData.print_access_token_file()
    __print_help_message()

def oauth_delete_access_token_file():
    """delete the access token file"""
    CO.AccessData.delete_access_token_file()
    __print_help_message()

def oauth_help():
    """ display a help message which shows what functions can be called"""
    if CO.AccessData.access_token_file_exists():
        logger.info('The client is authorised with Dropbox (Dropbox token file exists)')
        print """
You can run these functions:
 - oauth_help()                      # display this help message
 - oauth_debug(True_or_False)        # enable / disable printing of log messages
 - db_list_directory(dirpath)        # list the contents of directory dirpath (default '/')
 - db_print_file(filepath)           # eg '{account_info_file}' or '{review_file}'
 - db_create_text_file(filename)     # create a file and enter some lines into it
 - db_delete_file(filename)          # delete a file
 - db_disable_access_token()         # disable Dropbox access token (does not delete access file)
 - oauth_access_token_file_exists()  # should return True
 - oauth_print_access_token_file()   # print the contents of the token file
 - oauth_delete_access_token_file()  # remove the token file (re-authorisation will be required)
You can inspect or use these variables:
 - dropbox_client                    # a dropbox.client.DropboxClient() - try help(dropbox_client)
""".format(account_info_file=DW.ACCOUNT_INFO_FILE, review_file=DW.REVIEW_FILE)
    else:
        logger.info('The client is not yet authorised with Dropbox (the Dropbox token file does not exist)')
        print """
You can run these functions:
 - oauth_help()                      # display this help message
 - oauth_debug(True_or_False)        # enable / disable printing of log messages
 - oauth_access_token_file_exists()  # should return False
 - oauth_authorise_no_redirect()     # authorisation requiring auth code to be entered
 - oauth_authorise_redirect()        # authorisation using HTTP web server (simulates URL schema)
"""

if __name__ == '__main__':
    # load access data into module variable for use in Python interpreter
    if CO.AccessData.access_token_file_exists():
        access_data = CO.AccessData()
        access_data.load()
        dropbox_client = dropbox.client.DropboxClient(access_data.access_token)

    oauth_help()

