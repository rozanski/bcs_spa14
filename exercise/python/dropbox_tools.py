"""
dropbox_tools.py: methods for performing some Dropbox actions (creating or printing files etc)

You should import this file as follows so you can call its methods directly:
    from dropbox_tools import *

Dependencies:
    python 2.7
    dropbox - Python Dropbox API (https://www.dropbox.com/developers/datastore/sdks/python)

Global constants:
    ACCOUNT_INFO_FILE: This file is created in the Dropbox app folder once the client has authorised with Dropbox
        It contains information on the Dropbox user account
    REVIEW_DIRECTORY: This folder is created in the Dropbox app folder once the client has authorised with Dropbox
    REVIEW_FILE: This file is created in the Dropbox app folder once the client has authorised with Dropbox

Global variables:
    logger: used to log error, info and debug messages
        example usage: logger = CO.logger; logger('message')

Methods:
    db_list_directory(): list the contents of the given Dropbox directory
    db_print_file(): print the contents of the given file to stdout
    db_create_text_file(): create a file and write some lines of text to it
    db_create_sample_files(): create some files and directories in Dropbox directory

"""

from tempfile import NamedTemporaryFile
import dropbox

import common_oauth as CO
logger = CO.logger

ACCOUNT_INFO_FILE = '/account_info.python.txt'
REVIEW_DIRECTORY = '/oauth_session_python'
REVIEW_FILE = '%s/oauth_session_review.python.md' % REVIEW_DIRECTORY

def __create_dropbox_client():
    """create and return a Dropbox client for use in the functions below"""
    # EXERCISE:
    #  - create a Dropbox OAuth client object with which to make Dropbox calls
    #    hint: class is dropbox.client.DropboxClient()
    #  - this needs to be supplied with the access token
    #    hint: create a CO.AccessData() object and load() it from file
    #  - return this client to the caller
# TODO ==> INSERT CODE HERE <==

def db_list_directory(path='/'):
    """list the contents of the given Dropbox directory"""
    client, access_data = __create_dropbox_client()

    # EXERCISE:
    #  - make a Dropbox client call to get the dropbox directory contents
    #    hint: Dropbox calls this "folder metadata"
    #    hint: @see https://www.dropbox.com/developers/core/docs/python (search for metadata())
# TODO ==> INSERT CODE HERE <==

def db_delete_file(file_path=''):
    """delete the given dropbox file"""
    if file_path == '':
        file_path = raw_input('Enter the name of the file to delete: ').strip()
    client, access_data = __create_dropbox_client()

    client.file_delete(file_path)
    logger.debug('deleted file %s' % file_path)

def db_disable_access_token():
    """disable the dropbox access token (so authorisation will need to be run again"""
    client, access_data = __create_dropbox_client()

    client.disable_access_token()
    logger.info('disabled Dropbox access token (access file not deleted)')

def db_create_sample_files():
    """create some files and directories in Dropbox directory"""
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
    logger.debug('uploading account info file %s' % ACCOUNT_INFO_FILE)
    client.put_file(ACCOUNT_INFO_FILE, tempfile, overwrite=True)
    logger.info('uploaded account info file %s' % ACCOUNT_INFO_FILE)

    # create review subdirectory
    try:
        client.file_delete(REVIEW_DIRECTORY)
    except dropbox.rest.ErrorResponse as e:
        if e.status <> 404: raise e
    client.file_create_folder(REVIEW_DIRECTORY)
    logger.info('created review directory %s' % REVIEW_DIRECTORY)

    # save file containing session review
    tempfile = NamedTemporaryFile(delete=False)
    fname = tempfile.name
    logger.debug('writing session review to temporary file %s' % (fname))
    tempfile.write('# KEEPING PASSWORDS PRIVATE WITH OAUTH\n')
    tempfile.write('An interesting and thought-provoking session\n')
    tempfile.write('The presenters were top-notch and I learned a lot\n')
    tempfile.close()
    tempfile = open(fname)

    logger.debug('uploaded session review file %s' % REVIEW_FILE)
    client.put_file(REVIEW_FILE, tempfile, overwrite=True)
    logger.info('uploaded session review file %s' % REVIEW_FILE)

