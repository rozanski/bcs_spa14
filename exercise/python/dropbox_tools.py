"""
dropbox_tools.py: methods for performing some Dropbox actions (creating or printing files etc)

You should import this file as follows so you can call its methods directly::
    from dropbox_tools import *
"""

from tempfile import NamedTemporaryFile
import dropbox

import common_oauth as CO
logger = CO.logger
"""
used to log error, info and debug messages, for example::
    logger = CO.logger; logger('message')
"""

DB_ACCOUNT_INFO_FILE = '/account_info.python.txt'
"""
This file is created in the Dropbox app folder once the client has authorised with Dropbox.
It contains information on the Dropbox user account.
It is a Dropbox file path so uses forward-slash as directory separator, even on Windows
"""

DB_REVIEW_DIRECTORY = '/oauth_session_python'
"""
This folder is created in the Dropbox app folder once the client has authorised with Dropbox.
It is a Dropbox file path so uses forward-slash as directory separator, even on Windows
"""

DB_REVIEW_FILE = '%s/oauth_session_review.python.md' % DB_REVIEW_DIRECTORY
"""
This file is created in the Dropbox app folder once the client has authorised with Dropbox.
It is a Dropbox file path so uses forward-slash as directory separator, even on Windows
"""

suppress_help_message = False
def __print_help_message():
    """Print a help message"""
    global suppress_help_message
    if not suppress_help_message:
        print ('\nCommand complete. Type oauth_help() for help:\n')

def __create_dropbox_client():
    """Create and return a Dropbox client for use in the functions below"""
    # EXERCISE:
    #  - create a Dropbox OAuth client object with which to make Dropbox calls
    #    hint: class is dropbox.client.DropboxClient()
    #  - this needs to be supplied with the access token
    #    hint: create a CO.AccessData() object and load() it from file
    #  - return this client to the caller
    # note how you have not had to provide any user credentials during this process!
# TODO ==> INSERT CODE HERE <==
    __print_help_message()

def db_list_directory(path='/'):
    """
    List the contents of the given Dropbox directory
    @type path: string
    @param path: Dropbox path of the directory
    """
    client, access_data = __create_dropbox_client()

    # EXERCISE:
    #  - make a Dropbox client call to get the dropbox directory contents
    #    hint: Dropbox calls this "folder metadata"
    #    @see https://www.dropbox.com/developers/core/docs/python (search for metadata())
# TODO ==> INSERT CODE HERE <==
    __print_help_message()

def db_delete_file(file_path=''):
    """Delete the given Dropbox file
    @type file_path: string
    @param file_path: Dropbox path of the file
    """
    if file_path == '':
        file_path = raw_input('Enter the name of the file to delete: ').strip()
    client, access_data = __create_dropbox_client()

    client.file_delete(file_path)
    logger.debug('deleted file %s' % file_path)
    __print_help_message()

def db_disable_access_token():
    """
    Disable the dropbox access token (so authorisation will need to be run again
    See U{https://www.dropbox.com/developers/core/docs#disable-token}
    """
    client, access_data = __create_dropbox_client()

    client.disable_access_token()
    logger.info('disabled Dropbox access token (access file not deleted)')
    __print_help_message()

def db_create_sample_files():
    """Create some files and directories in Dropbox directory"""
    client, access_data = __create_dropbox_client()

    # save file containing account information
    tempfile = NamedTemporaryFile(delete=False)
    fname = tempfile.name
    logger.debug('writing account info to temporary file %s' % (fname))
    tempfile.write('ACCOUNT INFORMATION FOR USER %s\n' % (access_data.user_id))
    account_info = client.account_info()
    for info in account_info:
        tempfile.write("{parameter} = {value}\n".format(parameter=info, value=str(account_info[info])))
    tempfile.close()
    tempfile = open(fname)
    logger.debug('uploading account info file %s' % DB_ACCOUNT_INFO_FILE)
    client.put_file(DB_ACCOUNT_INFO_FILE, tempfile, overwrite=True)
    logger.info('uploaded account info file %s' % DB_ACCOUNT_INFO_FILE)

    # create review subdirectory
    try:
        client.file_delete(DB_REVIEW_DIRECTORY)
    except dropbox.rest.ErrorResponse as e:
        if e.status <> 404: raise e
    client.file_create_folder(DB_REVIEW_DIRECTORY)
    logger.info('created review directory %s' % DB_REVIEW_DIRECTORY)

    # save file containing session review
    tempfile = NamedTemporaryFile(delete=False)
    fname = tempfile.name
    logger.debug('writing session review to temporary file %s' % (fname))
    tempfile.write('# KEEPING PASSWORDS PRIVATE WITH OAUTH\n')
    tempfile.write('An interesting and thought-provoking session\n')
    tempfile.write('The presenters were top-notch and I learned a lot\n')
    tempfile.close()
    tempfile = open(fname)

    logger.debug('uploaded session review file %s' % DB_REVIEW_FILE)
    client.put_file(DB_REVIEW_FILE, tempfile, overwrite=True)
    logger.info('uploaded session review file %s' % DB_REVIEW_FILE)

    __print_help_message()

