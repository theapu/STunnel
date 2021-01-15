# STunnel
STunnel client for Android

```
Configuration
[connectionname]#Name of connection
client = yes# Set as client 
accept = 127.0.0.1:<port># Local host port for connecting to STunnel client 
connect = <IP>:<port>#STunnel server ip and port
CAfile = <path to certificate file>#Certificate file location in phone's internal memory
verify = 4#checks your peer certificate
```

Example for configuration to be filled in main window.

```
[openvpnca]
client = yes
accept = 127.0.0.1:9567
connect = 100.100.100.100:2222
CAfile = /storage/emulated/0/certs/stunnelCA.crt
verify = 4
```
