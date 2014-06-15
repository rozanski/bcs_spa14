#! /usr/bin/python
""" test_common_oauth.py: unit tests for common_oauth.py"""
# @see http://docs.python.org/2/library/unittest.html
# use @unittest.skip("not yet written") to skip a test

import logging
from mock import Mock
import types
import urllib

import unittest

from common_test import *

# module under test
import common_oauth as CO

http_services = CO.HttpServices()

class TestCommonOauth(CustomAssertions):

    def delete_oauth_files(self):
        for datafile in [ CO.AccessData.ACCESS_TOKEN_FILE,
                          http_services.HTTPD_SESSION_FILE,
                          http_services.HTTPD_SESSION_FILE_EXPIRED,
                          http_services.HTTPD_LATEST_URL_FILE ]:
            delete_file(datafile)

    def create_and_save_access_data(self, test_name):
        save_access_data = CO.AccessData('TEST %s' % (test_name))
        save_access_data.access_token = 'TOKEN %s' % (test_name)
        save_access_data.user_id = '12345678'
        save_access_data.save()
        self.assertTrue(path_exists(CO.AccessData.ACCESS_TOKEN_FILE), 'ACCESS_TOKEN_FILE not created by save()')
        return save_access_data

    def create_and_save_session_data(self, test_name):
        http_services.httpd_session[http_services.OAUTH_CSRF_SESSION_KEY] = 'SESSION %s' % (test_name)
        http_services.save_httpd_session()
        self.assertTrue(path_exists(http_services.HTTPD_SESSION_FILE), 'HTTPD_SESSION_FILE not created by save_httpd_session()')
        return http_services

    #####################
    # CUSTOM ASSERTIONS #
    #####################

    def assertAccessDataEqual(self, access_data1, description1, access_data2, description2):
        self.assertTrue(access_data1.access_token == access_data2.access_token,
            "{description1} access_token '{access_token1}' does not equal {description2} access_token '{access_token2}'".format(
                 description1=description1,
                 access_token1=access_data1.access_token,
                 description2=description2,
                 access_token2=access_data2.access_token))
        self.assertTrue(access_data1.user_id == access_data2.user_id,
            "{description1} user_id '{user_id1}' does not equal {description2} user_id '{user_id2}'".format(
                 description1=description1,
                 user_id1=access_data1.user_id,
                 description2=description2,
                 user_id2=access_data2.user_id))
        self.assertTrue(access_data1.save_message == access_data2.save_message,
            "{description1} save_message '{save_message1}' does not equal {description2} save_message '{save_message2}'".format(
                 description1=description1,
                 save_message1=access_data1.save_message,
                 description2=description2,
                 save_message2=access_data2.save_message))

    def assertSessionDataEqual(self, session_data1, description1, session_data2, description2):
        self.assertTrue(session_data1.httpd_session[http_services.OAUTH_CSRF_SESSION_KEY] ==
            session_data2.httpd_session[http_services.OAUTH_CSRF_SESSION_KEY],
            "{description1} session_data '{session_data1}' does not equal {description2} session_data '{session_data2}'".format(
                 description1=description1,
                 session_data1=session_data1.httpd_session[http_services.OAUTH_CSRF_SESSION_KEY],
                 description2=description2,
                 session_data2=session_data2.httpd_session[http_services.OAUTH_CSRF_SESSION_KEY]))

    #################
    # TEST FIXTURES #
    #################
    def setUp(self):
        """unit test fixture - setup"""
        CO.logger.setLevel(logging.CRITICAL)
        self.delete_oauth_files()
        TestCommonOauth.http_services = CO.HttpServices()

    def tearDown(self):
        """unit test fixture - tear down"""
        self.delete_oauth_files()

    #########################
    # Module constant tests #
    #########################
    def test_browser_defined(self):
        # @see https://docs.python.org/2/library/webbrowser.html
        valid_browsers = [ 'mozilla', 'firefox', 'netscape', 'galeon', 'epiphany', 'skipstone', 
                           'kfmclient', 'konqueror', 'kfm', 'mosaic', 'opera', 'grail', 'links', 'elinks', 
                           'lynx', 'w3m', 'windows-default', 'macosx', 'safari' ]
        self.assertTrue(CO.BROWSER_NAME in valid_browsers, 'Invalid browser %s' % (CO.BROWSER_NAME))

    def test_demo_directory(self):
        self.assertTrue(path_exists(CO.DEMO_DIRECTORY), 'DEMO_DIRECTORY does not exist')

    def test_files_directory(self):
        self.assertTrue(path_exists(CO.FILES_DIRECTORY), 'FILES_DIRECTORY does not exist')

    def test_doc_directory(self):
        self.assertTrue(path_exists(CO.DOC_DIRECTORY), 'DOC_DIRECTORY does not exist')

    #################
    # AppData tests #
    #################
    def test_app_data_app_key(self):
        self.assertTrue(type(CO.AppData.APP_KEY) == types.StringType, 'APP_KEY not a string')
        self.assertTrue(len(CO.AppData.APP_KEY) > 0, 'APP_KEY not a string')

    def test_app_data_app_secret(self):
        self.assertTrue(type(CO.AppData.APP_SECRET) == types.StringType, 'APP_SECRET not a string')
        self.assertTrue(len(CO.AppData.APP_SECRET) > 0, 'APP_SECRET not a string')

    ####################
    # AccessData tests #
    ####################
    def test_access_data_token_file_path(self):
        self.assertValidFilePath(CO.AccessData.ACCESS_TOKEN_FILE)

    def test_access_data_save_load(self):
        save_access_data = self.create_and_save_access_data('test_access_data_save_load')
        load_access_data = CO.AccessData('')
        load_access_data.load()
        self.assertAccessDataEqual(save_access_data, 'saved', load_access_data, 'loaded')

    def test_access_token_file_exists(self):
        access_data = self.create_and_save_access_data('test_access_token_file_exists')
        self.assertTrue(CO.AccessData.access_token_file_exists(True))
        delete_file(CO.AccessData.ACCESS_TOKEN_FILE)
        self.assertFalse(access_data.access_token_file_exists(True))

    def test_delete_access_token_file(self):
        access_data = self.create_and_save_access_data('test_delete_access_token_file')
        self.assertTrue(CO.AccessData.access_token_file_exists(True))
        CO.AccessData.delete_access_token_file()
        self.assertFalse(access_data.access_token_file_exists(True))

    ######################
    # HttpServices tests #
    ######################
    def test_http_services_init(self):
        self.assertUrlValid(http_services.OAUTH_HOME_URL, 'OAUTH_HOME_URL')
        self.assertUrlValid(http_services.OAUTH_FINISH_URL, 'OAUTH_FINISH_URL')

    def test_http_services_reachable(self):
        self.assertUrlReachable(http_services.OAUTH_HOME_URL, 'OAUTH_HOME_URL')

    def test_http_services_session_file_path(self):
        self.assertValidFilePath(http_services.HTTPD_SESSION_FILE)
        self.assertValidFilePath(http_services.HTTPD_SESSION_FILE_EXPIRED)

    def test_http_services_session_file_save_load(self):
        save_session_data = self.create_and_save_session_data('test_http_services_session_file_save_load')
        load_session_data = CO.HttpServices()
        load_session_data.load_httpd_session()
        self.assertSessionDataEqual(save_session_data, 'saved', load_session_data, 'loaded')

    def test_http_services_session_file_expire(self):
        save_session_data = self.create_and_save_session_data('test_http_services_session_file_save_load')
        save_session_data.expire_httpd_session()
        self.assertFalse(path_exists(http_services.HTTPD_SESSION_FILE), 'HTTPD_SESSION_FILE not renamed')
        self.assertTrue(path_exists(http_services.HTTPD_SESSION_FILE_EXPIRED), 'HTTPD_SESSION_FILE_EXPIRED does not exist')

    def test_http_services_session_file_delete(self):
        save_session_data = self.create_and_save_session_data('test_http_services_session_file_save_load')
        save_session_data.delete_httpd_session_file()
        self.assertFalse(path_exists(http_services.HTTPD_SESSION_FILE), 'HTTPD_SESSION_FILE not deleted')

    def test_http_services_session_file_expired_delete(self):
        save_session_data = self.create_and_save_session_data('test_http_services_session_file_save_load')
        save_session_data.delete_httpd_session_file()
        self.assertFalse(path_exists(http_services.HTTPD_SESSION_FILE_EXPIRED), 'HTTPD_SESSION_FILE_EXPIRED not deleted')

    def test_http_services_open_browser_window(self):
        http_services.clear_latest_url()
        CO.HttpServices.open_browser_window(http_services.OAUTH_HOME_URL)
        url_file_found = http_services.wait_for_latest_url_file(5)
        self.assertTrue(url_file_found, 'Failed to open home page %s' % (http_services.OAUTH_HOME_URL))

if __name__ == "__main__":
    test_outcome = TestOutcome(unittest.main(verbosity=TEST_VERBOSITY, exit=False), __file__)
    test_outcome.print_summary()

