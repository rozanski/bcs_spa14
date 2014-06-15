""" common_test.py: shared code for unit testing"""

import os, os.path
import sys
import types
import urllib
import StringIO

import unittest

import common_oauth as CO

TEST_USER_ID = '1014682'
TEST_ACCESS_TOKEN = '9gu6UWQLjvEAAAAAAAAGBBfj-0YRipPAIzkElaJwh1HncZDUF4wodCIH3yfK7b7l'
TEST_VERBOSITY = 1
# TEST_VERBOSITY = 2 # gives more detail on tests as they are run

def touch_file(filepath):
    if os.path.exists(filepath):
        os.utime(filepath, None)
    else:
        open(filepath, 'a').close()

def delete_file(filepath):
    if os.path.exists(filepath): os.remove(filepath)

def path_exists(filepath):
    return os.path.exists(filepath)

def create_test_token_file():
    with open(CO.AccessData.ACCESS_TOKEN_FILE, 'w') as fp:
        fp.write(
"""{{
    "access_token": "{access_token}", 
    "creation_time": "{time_now}", 
    "message": "created for Python unit tests", 
    "user_id": "{user_id}"
}}
""".format(access_token=TEST_ACCESS_TOKEN, time_now=CO.time_now(), user_id=TEST_USER_ID))

def delete_test_token_file():
    delete_file(CO.AccessData.ACCESS_TOKEN_FILE)

def capture_stdio(capture_on):
    global save_stdio
    if capture_on:
        save_stdio = (sys.stdout, sys.stderr)
        sys.stdout = StringIO.StringIO()
        sys.stderr = StringIO.StringIO()
    else:
        sys.stdout, sys.stderr = save_stdio

class TestOutcome(object):
    """
    class to return dictionary of test outcome

    Usage:
        import nr_unittest as UT
        test_result = unittest.main(exit=False, ...)
        result_dict = UT.TestOutcome(test_result)
    """
    def __init__(self, unittest_main_result, filepath):
        self.successful = unittest_main_result.result.wasSuccessful()
        self.run = unittest_main_result.result.testsRun
        self.skipped = len(unittest_main_result.result.skipped)
        self.errors = len(unittest_main_result.result.errors)
        self.failures = len(unittest_main_result.result.failures)
        self.filepath = filepath

    def print_summary(self):
        module_name = os.path.basename(self.filepath)
        msg = '\nUnit tests complete for %s: run=%d, skipped=%d' % (module_name, self.run, self.skipped)
        if self.failures > 0: msg += ', FAILED=%d' % (self.failures)
        if self.errors > 0: msg += ', ERRORS=%d' % (self.errors)
        msg += (('\nALL TESTS FOR %s PASSED' % (module_name)) if self.successful else ('\nSOME TESTS FOR %s FAILED!') % (module_name))
        print msg

class CustomAssertions(unittest.TestCase):

    #####################
    # CUSTOM ASSERTIONS #
    #####################
    def assertValidFilePath(self, path):
        self.assertTrue(type(path) == types.StringType, 'path %s not a string' % (path))
        touch_file(path)
        self.assertTrue(os.path.exists(path), 'path %s is invalid or not writeable' % (path))
        os.remove(path)

    def assertUrlValid(self, url, description):
        url_regex = 'http://[a-zA-Z0-9_\.]*:\d*/[a-zA-Z0-9_\-\.]*'
        self.assertRegexpMatches(url, url_regex, "%s URL '%s' is not valid" % (description, url))

    def assertUrlReachable(self, url, description):
        http_status = 503
        try:
            http_status = urllib.urlopen(url).getcode()
            self.assertTrue((http_status in [200, 301]), "%s URL '%s' is not reachable (status=%d)" % (url, description, http_status))
        except IOError as e:
            self.assertTrue(False, "%s URL '%s' is not reachable (status=%d)" % (url, description, http_status))

    def assertStdoutContains(self, expected_content):
        """assert expected_content has been written to stdout"""
        if type(expected_content) is not types.ListType:
            expected_content = [ expected_content ]
        stdout_message = sys.stdout.getvalue()
        for the_text in expected_content:
            self.assertIn(the_text, stdout_message,('Stdout "%s" does not contain text "%s"' % (stdout_message, the_text)))

    def assertStdoutDoesNotContain(self, unexpected_content):
        """assert unexpected_content has not been written to stdout"""
        if type(unexpected_content) is not types.ListType:
            unexpected_content = [ unexpected_content ]
        stdout_message = sys.stdout.getvalue()
        for the_text in unexpected_content:
            self.assertNotIn(the_text, stdout_message,('Stdout "%s" contains text "%s"' % (stdout_message, the_text)))

