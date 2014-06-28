# ERRATA

## pip Installation Errors
You may see errors like this when installing pip:

    $ pip install mock
    Downloading/unpacking mock
        Cannot fetch index base URL https://pypi.python.org/simple/
        Could not find any downloads that satisfy the requirement mock
        Cleaning up...
        No distributions at all found for mock
        Storing debug log for failure in /Users/xxxxx/.pip/pip.log

This appears to be an SSL certificate verification error.
We have only seen it on one copmuter and in the most recent version of pip.

### Workaround
PyPi uses the DigiCert set (https://ev-root.digicert.com/info/index.html).
We have included a copy of this certificate in the `etc/` directory.

Pass the root certificate `etc/digicert-highassurance-root.pem` to pip in order to download files.

    pip --cert digicert-highassurance-root.pem install mock

