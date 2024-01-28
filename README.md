# Remote Monitoring

### Mobile Application
You can install the latest app from this repository releases. [Release Page](https://github.com/KeyMoad/remote-monitoring-agent/releases)

To use the app features, after installing app, you should register your server in register page.

__Please ensure that the agent is installed successfully__

### Agent Api
You can install the agent on your server by running this command:
```
bash <( curl -sSL https://raw.githubusercontent.com/KeyMoad/remote-monitoring-agent/main/api/installer.sh [arg] )
```

#### usage:
    e.g. installer.sh install
    Options:
        help        Show list of arguments and usage informations.
        install     Install the RMAgent on your mechine.
        update      Update RMAgent. Please use it if your agent version is not latest!
        uninstall   Uninstall the RMAgent service and all dependencies from your mechine.

## Released versions

| Section       | Version           |
| ------------- |:-----------------:|
| Mobile app    | 1.0.0 - latest    |
| Agent api     | 1.0.0 - latest    |
