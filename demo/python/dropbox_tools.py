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

ACCOUNT_INFO_FILE = '/account_info.python.txt'
"""
This file is created in the Dropbox app folder once the client has authorised with Dropbox.
It contains information on the Dropbox user account
"""

REVIEW_DIRECTORY = '/oauth_session_python'
"""This folder is created in the Dropbox app folder once the client has authorised with Dropbox"""

REVIEW_FILE = '%s/oauth_session_review.python.md' % REVIEW_DIRECTORY
"""This file is created in the Dropbox app folder once the client has authorised with Dropbox"""

def __create_dropbox_client():
    """create and return a Dropbox client for use in the functions below"""
    # EXERCISE:
    #  - create a Dropbox OAuth client object with which to make Dropbox calls
    #    hint: class is dropbox.client.DropboxClient()
    #  - this needs to be supplied with the access token
    #    hint: create a CO.AccessData() object and load() it from file
    #  - return this client to the caller
    # note how you have not had to provide any user credentials during this process!
    # SPA14_OAUTH_START
    access_data = CO.AccessData()
    access_data.load()
    return dropbox.client.DropboxClient(access_data.access_token), access_data
    # SPA14_OAUTH_FINISH

def db_list_directory(path='/'):
    """
    list the contents of the given Dropbox directory
    @type path: string
    @param path: Dropbox path of the directory
    """
    client, access_data = __create_dropbox_client()

    # EXERCISE:
    #  - make a Dropbox client call to get the dropbox directory contents
    #    hint: Dropbox calls this "folder metadata"
    #    @see https://www.dropbox.com/developers/core/docs/python (search for metadata())
    # SPA14_OAUTH_START
    folder_metadata = client.metadata(path)
    print 'DIRECTORIES in %s:' % (path,)
    for entry in folder_metadata['contents']:
        if entry['is_dir']: print ' %s   %s/' % (entry['modified'], entry['path'][1:])
    print 'FILES IN %s:' % (path,)
    for entry in folder_metadata['contents']:
        if not entry['is_dir']: print ' %s   %s (%s)' % (entry['modified'], entry['path'][1:], entry['size'])

def db_print_file(file_path):
    """
    print the contents of the given file to stdout
    @type file_path: string
    @param file_path: Dropbox path of the file
    """
    client, access_data = __create_dropbox_client()

    print 'CONTENTS OF %s:' % (file_path,)
    line_count = 1
    with client.get_file(file_path) as f:
        for line in f.readlines():
            print '{num:>2}: {line}'.format(num=line_count, line=line.strip('\n'))
            line_count += 1

def db_create_text_file(file_path='', lines=[]):
    """
    create a file and write some lines of text to it from stdin or lines[]
    @type file_path: string
    @param file_path: Dropbox path of the file to create
    @type lines: string[]
    @param lines: lines to write in the file
    """
    client, access_data = __create_dropbox_client()

    if file_path == '':
        file_path = raw_input('Enter the name of a file to create: ').strip()
    tempfile = NamedTemporaryFile(delete=False)
    fname = tempfile.name
    if len(lines) ==0:
        while True:
            line = raw_input('Enter some text (blank to finish): ').strip('\n')
            if line.strip() == '': break
            tempfile.write('%s\n' % (line,))
    else:
        for line in lines:
            tempfile.write('%s\n' % (line,))
    tempfile.close()
    logger.debug('creating file %s...' % file_path)

    # EXERCISE:
    #  - open() the temporary file fname for read
    #  - pass this file handle to Dropbox put_file() to upload the file
    #    @see https://www.dropbox.com/developers/core/docs/python (search for put_file())
    # SPA14_OAUTH_START
    tempfile = open(fname)
    client.put_file(file_path, tempfile, overwrite=True)
    logger.debug('created file %s' % file_path)
    # SPA14_OAUTH_FINISH

def db_delete_file(file_path=''):
    """delete the given dropbox file
    @type file_path: string
    @param file_path: Dropbox path of the file
    """
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

