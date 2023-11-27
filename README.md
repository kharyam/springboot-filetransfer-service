# File Transfer

Simple file transfer web service in spring boot

Define the directories within [application.yaml](src/main/resources/application.yaml)

e.g.,
```yaml
mydatadir: /opt/data/uploads
```

Use the key value within web service calls:

Examples with curl:
```bash
# Create a test file for upload
echo "hello world" > test.txt

# Upload a file.  The key must be "file" as shown below
curl -X POST -F file=@test.txt http://localhost:8080/filetransfer/mydatadir 

# List the directory
curl http://localhost:8080/filetransfer/mydatadir

# Download a file
curl http://localhost:8080/filetransfer/mydatadir/test.txt
```

## TODO

Configure mutual authentication (mTLS) for security. Settings are stubbed out in [application.yaml](src/main/resources/application.yaml)