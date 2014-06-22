#! /usr/bin/python
""" test_dropbox_tools.py: unit tests for dropbox_tools.py"""
# @see http://docs.python.org/2/library/unittest.html
# use @unittest.skip("not yet written") to skip a test

import logging
from mock import Mock
import types
import urllib
import uuid

import unittest

import dropbox

from common_test import *
import common_oauth as CO

# module under test
import dropbox_tools as DT
DT.suppress_help_message = True

class TestDropboxTools(CustomAssertions):

    TEST_DROPBOX_FILEPATH = '/unittest.%s.%s.python.txt'

    def create_test_dropbox_file(self, number_of_lines, test_id):
        lines = []
        for line_count in range(0, number_of_lines):
            lines.append('%s line %d' % (self.TEST_DROPBOX_FILEPATH, line_count))
        # ensure that filenames are unique since all testers are using my Dropbox
        file_path = self.TEST_DROPBOX_FILEPATH % (test_id, uuid.uuid1())
        DT.db_create_text_file(file_path, lines)
        return (file_path, '\n'.join(lines) + '\n') # join doesn't put a \n at the end

    #####################
    # CUSTOM ASSERTIONS #
    #####################
    def assertDbFileHasBeenDeleted(self, dropbox_file):
        global dropbox_client
        with self.assertRaises(dropbox.rest.ErrorResponse) as cm:
            file_and_metadata = dropbox_client.get_file_and_metadata(dropbox_file)
        the_exception = cm.exception
        self.assertEqual(the_exception.status, 404,
                ('Dropbox file %s does not appear to have deleted' %  (dropbox_file)))
        self.assertEqual(the_exception.error_msg, 'File has been deleted',
                ("File deletion - expecting error message 'File has been deleted', got '%s'" %  (the_exception.error_msg)))

    def assertDbFileContentsEqual(self, dropbox_file, expected_contents):
        global dropbox_client
        with dropbox_client.get_file(dropbox_file) as f:
                actual_contents = f.read()
        self.assertTrue(actual_contents == expected_contents,
                ("File contents '%s' do not match expected contents '%s'" % (actual_contents, expected_contents)))

    #################
    # TEST FIXTURES #
    #################
    def setUp(self):
        """unit test fixture - setup"""
        CO.logger.setLevel(logging.CRITICAL)
        global dropbox_client
        dropbox_client = dropbox.client.DropboxClient(TEST_ACCESS_TOKEN)
        create_test_token_file()

    def tearDown(self):
        """unit test fixture - tear down"""
        delete_test_token_file()

    ##############
    # unit tests #
    ##############
    def test_db_create_text_file(self):
        dropbox_file, lines_string = self.create_test_dropbox_file(6, self.id())
        self.assertDbFileContentsEqual(dropbox_file, lines_string)
        DT.db_delete_file(dropbox_file)

    def test_db_list_directory(self):
        dropbox_file, lines_string = self.create_test_dropbox_file(4, self.id())
        capture_stdio(True)
        DT.db_list_directory()
        self.assertStdoutContains(dropbox_file[1:]) # remove leading '/'
        capture_stdio(False)
        DT.db_delete_file(dropbox_file)

    def test_db_print_file(self):
        dropbox_file, lines_string = self.create_test_dropbox_file(5, self.id())
        capture_stdio(True)
        DT.db_print_file(dropbox_file)
        self.assertStdoutContains('\n'.split(lines_string))
        capture_stdio(False)
        DT.db_delete_file(dropbox_file)

    def test_db_delete_file(self):
        dropbox_file, lines_string = self.create_test_dropbox_file(7, self.id())
        DT.db_delete_file(dropbox_file)
        self.assertDbFileHasBeenDeleted(dropbox_file)

    @unittest.skip("don't know how to test this since can't re-enable a token in Dropbox")
    def test_db_disable_access_token(self):
        pass

if __name__ == "__main__":
    test_outcome = TestOutcome(unittest.main(verbosity=TEST_VERBOSITY, exit=False), __file__)
    test_outcome.print_summary()

