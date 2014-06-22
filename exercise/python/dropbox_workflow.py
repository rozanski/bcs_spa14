"""
dropbox_workflow.py: methods to implement the Dropbox No-Redirect and Redirect workflows
"""

from tempfile import NamedTemporaryFile

import dropbox

import common_oauth as CO
from dropbox_tools import *

logger = CO.logger
"""
used to log error, info and debug messages, for example::
    logger = CO.logger; logger('message')
"""

class DropboxStatus(object):
    """Simple class for returning Dropbox status"""

    def __init__(self, http_status, message='', redirect_url=''):
        """
        @type http_status: int
        @param http_status: the HTTP status code (eg 200 for success)
        @type message: string
        @param message: an information message
        @type redirect_url: string
        @param redirect_url: optional parameter containing a redirection URL
        """
        self.http_status = http_status
        """the HTTP status code (eg 200 for success)"""
        self.message = message
        """an information message"""
        self.redirect_url = redirect_url
        """optional parameter containing a redirection URL"""
        self.time_now = CO.time_now()
        """set to the time the object was created"""

###############################################
# AUTHORISE WITHOUT AUTOMATIC URL REDIRECTION #
###############################################

def no_redirect_client_start():
    """
    This function implements the C{start()} portion of the Dropbox OAuth no-redirect flow.

    It passes the necessary parameters to Dropbox C{start()} and returns the generated URL to the caller.
    """
    logger.debug('starting Dropbox authorisation (no-redirect mode)')
    logger.debug('creating DropboxOAuth2FlowNoRedirect client for app "{app_name}" with key "{key}" and secret "{secret}"'.format(
                app_name=CO.AppData.APP_NAME, key=CO.AppData.APP_KEY, secret=CO.AppData.APP_SECRET))

    # EXERCISE:
    #  - create an OAuth no-redirect object (the class is DropboxOAuth2FlowNoRedirect)
    #    hint: get the app key and secret from CO.AppData
    #  - start the dropbox OAuth no-redirect workflow by calling start()
    #  - save the returned redirect URL in authorise_url
# TODO ==> INSERT CODE HERE <==

    logger.info('Dropbox authorisation start successful, got authorisation URL')
    logger.debug('authorisation URL="{url}"'.format(url=authorise_url))
    return DropboxStatus(301, redirect_url=authorise_url)

def no_redirect_client_finish_and_save(security_code):
    """
    This function implements the C{finish()} portion of the Dropbox OAuth no-redirect flow.

    It calls Dropbox C{finish()} to finish the Oauth workflow.
    It then saves the access token returned by C{finish()} and creates some sample files in the Dropbox app folder.
    """
    logger.info('finishing Dropbox authorisation (no-redirect mode), security code ="%s"' %(security_code,))
    logger.debug('creating DropboxOAuth2FlowNoRedirect client for app "{app_name}" with key "{key}" and secret "{secret}"'.format(
                app_name=CO.AppData.APP_NAME, key=CO.AppData.APP_KEY, secret=CO.AppData.APP_SECRET))

    # EXERCISE:
    #  - create an OAuth no-redirect object (as you did for no_redirect_client_start)
    #    hint: get the app key and secret from CO.AppData
    #  - finish the dropbox OAuth no-redirect workflow by calling finish()
    #    (pass it the security code that was entered by the user when the visited the Dropbox website)
    #  - store the returned access token and user id in a CO.AccessData() object
# TODO ==> INSERT CODE HERE <==

    logger.info('Dropbox authorisation finish successful, access token={access_token}, user id={user_id}'.format(
        access_token=access_data.access_token, user_id=access_data.user_id))
    access_data.save()
    db_create_sample_files()
    return access_data

############################################
# AUTHORISE WITH AUTOMATIC URL REDIRECTION #
############################################

def redirect_client_start():
    """
    This function implements the C{start()} portion of the Dropbox OAuth redirect flow.

    It passes the necessary parameters to dropbox C{start()} and returns the generated URL to the caller.
    """
    logger.debug('starting Dropbox authorisation (redirect mode)')
    logger.debug('creating DropboxOAuth2Flow client for app "{app_name}" with key "{key}" and secret "{secret}"'.format(
                app_name=CO.AppData.APP_NAME, key=CO.AppData.APP_KEY, secret=CO.AppData.APP_SECRET))
    httpd_services = CO.HttpServices()

    # EXERCISE:
    #  - create an OAuth redirect object (the class is DropboxOAuth2Flow)
    #    hint: get the app key and secret from CO.AppData
    #    hint: get the finish URL from the httpd_services object
    #    hint: the CSRF session variable is httpd_services.httpd_session
    #    hint: the CSRF session key is httpd_services.OAUTH_CSRF_SESSION_KEY
    #  - start the dropbox OAuth redirect workflow by calling start()
    #  - save the returned redirect URL in authorise_url
# TODO ==> INSERT CODE HERE <==

    logger.info('Dropbox authorisation start successful, authorisation URL="{url}"'.format(url=authorise_url))
    logger.debug('CSRF token="{token}", HTTP session is "{session}"'.format(
            token=httpd_services.httpd_session[httpd_services.OAUTH_CSRF_SESSION_KEY],session=str(httpd_services.httpd_session)))
    httpd_services.save_httpd_session()
    return DropboxStatus(301, redirect_url=authorise_url)

def httpd_handle_finish_and_save(request_path, query_dict):
    """
    This httpd handler implements the finish() portion of the Dropbox OAuth server (redirect) flow.

    It calls Dropbox C{finish()} to finish the Oauth workflow.
    It then saves the access token returned by C{finish()} and creates some sample files in the Dropbox app folder.
    """
    httpd_services = CO.HttpServices()
    httpd_services.load_httpd_session()
    logger.debug('finishing Dropbox authorisation (redirect mode), URL query="%s"' % (str(query_dict),))

    # EXERCISE:
    #  - create an OAuth no-redirect object (as you did for redirect_client_start)
    #    hint: get the app key and secret from CO.AppData
# TODO ==> INSERT CODE HERE <==

    try:
        # EXERCISE:
        #  - finish the dropbox OAuth no-redirect workflow by calling finish()
        #    (pass it the URL query dict{} that was used in the redirect to the HTTP server)
        #  - store the returned access token and user id in a CO.AccessData() object
        #  - (this demo ignores the "URL state" variable)
# TODO ==> INSERT CODE HERE <==

        httpd_services.expire_httpd_session()
        logger.info('Dropbox authorisation finish successful, access token="{access_token}", user id="{user_id}"'.format(
            access_token=access_data.access_token, user_id=access_data.user_id))
        access_data.save()
        db_create_sample_files()
        return DropboxStatus(200,
                '<h1>Congratulations!</h1><p>The Dropbox access token was created successfully.<p>You may return to your client.')
    except dropbox.client.DropboxOAuth2Flow.BadRequestException as e:
        logger.error('do_GET.finish-handler: 400 bad request "%s"' % (request_path,))
        httpd_services.expire_httpd_session()
        return DropboxStatus(400, 'Bad Request')
    except dropbox.client.DropboxOAuth2Flow.BadStateException as e:
        logger.error('do_GET.finish-handler: bad state, request="%s"' % (request_path,))
        httpd_services.expire_httpd_session()
        return DropboxStatus(301, redirect_url=OAUTH_START_URL)
    except dropbox.client.DropboxOAuth2Flow.CsrfException as e:
        logger.error('do_GET.finish-handler: 403 missing CSRF token, request="%s"' % (request_path,))
        httpd_services.expire_httpd_session()
        return DropboxStatus(403, 'Forbidden')
    except dropbox.client.DropboxOAuth2Flow.NotApprovedException as e:
        logger.error('do_GET.finish-handler: user did not approve, request="%s"' % (request_path,))
        httpd_services.expire_httpd_session()
        return DropboxStatus(301, redirect_url=OAUTH_START_URL)
    except dropbox.client.DropboxOAuth2Flow.ProviderException as e:
        logger.error('do_GET.finish-handler: 403 Dropbox authorisation error %s, request="%s"' % (e, path))
        httpd_services.expire_httpd_session()
        return DropboxStatus(403, 'Forbidden')

