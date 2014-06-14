#! /usr/bin/python
"""
httpd.py: simple webserver for use in Dropbox Python OAuth API demo

This class implements a simple HTTP server on the computer running the demo.
The server listens on host {@code HttpConfig.HTTP_SERVER} and port {@code HttpConfig.HTTP_PORT}.
It runs indefinitely until the user presses Enter or interrupts using control-C.

It serves various URLs, including:
- HttpConfig.HOME_PAGE - display a home page (use this to test that the server is running ok)
- HttpConfig.FINISH_PAGE - run the finish step of the Dropbox redirect workflow.

Global variables:
    logger: used to log error, info and debug messages
        example usage: logger = CO.logger; logger('message')

@see http://www.codeproject.com/Articles/462525/Simple-HTTP-Server-and-Client-in-Python
"""

from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
from urlparse import *
import glob, os.path

import common_oauth as CO
import logging
logger = CO.logger
import dropbox_workflow as DW

class OAuthHTTPRequestHandler(BaseHTTPRequestHandler):
    """ request handler which will receive redirected OAuth replies

    This class performs the necessary interactions between the web browser and the web server.

    Methods:
        do_GET(): process an HTTP GET
        handle_dropbox_status(): handle a Dropbox status for do_GET()
        send_http_header(): send HTTP header, either 200 (success) or 301 (redirect) if url is not None
                                  also sends headers to prevent caching in the browser
        send_html_content(): send HTML including the given page body to the browser
    """

    def send_http_header(self, url=None):
        """send HTTP header, either 200 (success) or 301 (redirect) if url is not None"""
        if url is None:
            logger.debug('sending HTTP success (200) header')
            self.send_response(200)
            self.send_header('Content-type', 'text/html')
        else:
            url = url.encode('ascii', 'ignore')
            logger.debug("sending HTTP redirect (301) header to URL '%s" % (url))
            self.send_response(301)
            self.send_header('Location', url)
        self.send_header('Cache-Control', 'no-cache, no-store, must-revalidate')
        self.send_header('Pragma', 'no-cache')
        self.send_header('Expires', '0')
        self.end_headers()

    def send_html_content(self, page_body):
        """send HTML including the given page body to the browser"""
        content="""<html>
<head><title>BCS SPA 2014 OAuth Demo</title></head>
<body>{body}<p><i>{time_now}</i></p></body>
</html>""".format(body=page_body, time_now=CO.time_now())
        self.wfile.write(content)

    def handle_dropbox_status(self, dropbox_status):
        """handle a Dropbox status for do_GET()"""
        if dropbox_status.http_status == 200:
            logger.debug('do_GET.handle_dropbox_status: sending 200 response')
            self.send_http_header()
            page_body=('<h1>Dropbox OAuth Authorisation Successful</h1><p>%s</p>' % dropbox_status.message) \
                    if dropbox_status.message <> '' else '(this page intentionally left blank)'
            self.send_html_content(page_body)
        elif dropbox_status.http_status == 301:
            logger.debug('do_GET.handle_dropbox_status: redirecting to %s', dropbox_status.redirect_url)
            self.send_http_header(dropbox_status.redirect_url)
        else:
            logger.debug('do_GET.handle_dropbox_status: returning error {error} "{message}"'.format(
                error=dropbox_status.http_status, message=dropbox_status.message))
            self.send_error(dropbox_status.http_status, dropbox_status.message)

    def do_GET(self):
        """process an HTTP GET"""

        logger.debug('do_GET: request is "%s"' % (self.path,))
        http_services = CO.HttpServices()
        http_services.save_latest_url(self.path)
        try:
            parsed_url = urlparse(self.path) # this is a named tuple
            logger.debug('do_GET: parsed URL is %s' % (str(parsed_url),))
            # this code turns the query into a dict of the form 'query_param' : 'query_value'
            query_dict = { query_param[0]: query_param[1][0] for query_param in parse_qs(parsed_url.query).items() }
            logger.debug('do_GET: query dict is %s' % (str(query_dict),))

            if parsed_url.path.endswith(http_services.OAUTH_FINISH_PAGE):
                logger.debug('do_GET.finish-handler: path is "{path}", query is "{query}"'.format(
                    path=self.path, query=query_dict))
                dropbox_status = DW.httpd_handle_finish_and_save(self.path, query_dict)
                logger.info('do_GET.start-handler: dropbox finish() successfully handled')
                self.handle_dropbox_status(dropbox_status)

            elif parsed_url.path.endswith('favicon.ico'):
                logger.debug('do_GET: /favicon.ico (not found)')
                self.send_error(404, 'Not Found')

            elif parsed_url.path.endswith('home'):
                logger.debug('do_GET: /home (send home page)')
                self.send_http_header()
                self.send_html_content(self.home_page_body(self.path))

            elif parsed_url.path.startswith('/doc/'):
                logger.debug('do_GET: /doc (script documentation), path is %s')
                if (parsed_url.path == '/doc/'):
                    logger.debug('do_GET: invalid documentation URL %s, redirecting', parsed_url.path)
                    self.send_http_header('/home')
                else:
                    module_path = parsed_url.path.split('/')
                    module_file = module_path[2]
                    file_type = module_file.split('.')[1]
                    logger.debug('do_GET: module path is %s, module file is %s', str(module_path), module_file)
                    self.send_http_header()
                    # with file('{demodir}/doc/{module_file}'.format(
                           #  demodir=CO.DEMO_DIRECTORY, module_file=module_file)) as f:
                    with file(os.path.join(CO.DOC_DIRECTORY, module_file)) as f:
                        file_content = f.read()
                    if file_type == 'txt':
                        file_content = '<html><head><title>BCS SPA 2014 OAuth Demo</title><body><pre>%s</pre></body></html> ' % (file_content)
                    self.wfile.write(file_content)

            else:
                self.send_error(404, 'unsupported request {request}'.format(request=self.path))

        except IOError:
            self.send_error(500, 'internal server error (request={request})'.format(request=self.path))

    def home_page_body(self, request):
        """return the body of the home page"""

        def make_anchor(url, newWindow=True):
            """return an HTML <a> tag"""
            return '<a href="{url}" {target}>{url}</a>'.format(
                url=url, target=('target="_blank"' if newWindow else ''))

        page_body="""
<h1>BCS SPA 2014 OAuth Demo</h1>
 <p>This is the local home page for the OAuth Demo.
 If you can read this your Python HTTP server is running successfully.</p>
<h2>Configuration</h2>
 APP_NAME: <code>{APP_NAME}</code><br>
 DEMO_DIRECTORY: <code>{DEMO_DIRECTORY}</code><br>
 FILES_DIRECTORY: <code>{FILES_DIRECTORY}</code><br>
 APP_KEY: <code>{APP_KEY}</code><br>
 APP_SECRET: <code>{APP_SECRET}</code><br>
 ACCESS_TOKEN_FILE: <code>{ACCESS_TOKEN_FILE}</code>
<h2>Help</h2>
 <dl>
  <dt>SPA conference session page:</dt><dd>{spapage}</dd>
  <dt>Dropbox Python SDK:</dt><dd>{apidocs}</dd>
  <dt>Python language reference:</dt><dd>{pythonref}</dd>
  <dt>Dropbox developer page for demo app:</dt><dd>{APP_WEBSITE}</dd>
 </dl>
<h2>Documentation</h2>
""".format(APP_NAME=CO.AppData.APP_NAME, DEMO_DIRECTORY=CO.DEMO_DIRECTORY, FILES_DIRECTORY=CO.FILES_DIRECTORY,
                    APP_KEY=CO.AppData.APP_KEY, APP_SECRET=CO.AppData.APP_SECRET,
                    ACCESS_TOKEN_FILE=CO.AccessData.ACCESS_TOKEN_FILE,
                    spapage=make_anchor('http://spaconference.org/spa2014/sessions/session576.html'),
                    apidocs=make_anchor('https://www.dropbox.com/developers/core/docs/python'),
                    pythonref=make_anchor('https://docs.python.org/2/reference/index.html'),
                    APP_WEBSITE=make_anchor(CO.AppData.APP_WEBSITE)
          )
        page_body += '<dl><dt>Pydoc HTML format:</dt><dd>'
        for htmlfile in glob.glob('{doc_dir}{sep}*.html'.format(doc_dir=CO.DOC_DIRECTORY, sep=os.sep)):
            page_body += '%s<br>' % (make_anchor('http://{server}:{port}/doc/{module_name}'.format(
                                            server=http_services.OAUTH_HTTPD_SERVER,
                                            port=http_services.OAUTH_HTTPD_PORT,
                                            module_name=os.path.basename(htmlfile)), False))
        page_body += '</dd><dt>Pydoc text format:</dt><dd>'
        for txtfile in glob.glob('{doc_dir}{sep}*.txt'.format(doc_dir=CO.DOC_DIRECTORY, sep=os.sep)):
            page_body += '%s<br>' % (make_anchor('http://{server}:{port}/doc/{module_name}'.format(
                                            server=http_services.OAUTH_HTTPD_SERVER,
                                            port=http_services.OAUTH_HTTPD_PORT,
                                            module_name=os.path.basename(txtfile)), False))
        page_body += '</dd><p>'
        return page_body

if __name__ == '__main__':
    http_services = CO.HttpServices()
    httpd = HTTPServer((http_services.OAUTH_HTTPD_SERVER, http_services.OAUTH_HTTPD_PORT), OAuthHTTPRequestHandler)
    logger.info('About to start the httpd server on "{server}" listening on port {port}...'.format(
        server=http_services.OAUTH_HTTPD_SERVER, port=http_services.OAUTH_HTTPD_PORT))
    logger.info('Browse to the home page "%s" to test the server' % http_services.OAUTH_HOME_URL)
    logger.info('Press <Ctrl-C> to stop the server')
    try:
        httpd.serve_forever()
    except KeyboardInterrupt:
        print ''
        logger.info('stopping httpd...')
    httpd.server_close()

