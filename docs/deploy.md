- Import GPG signing key (key id is `9ADA86837C216DC9AC71D9F35B5E50B8DD8E9063`)
- Configure credentials for `central-publishing-maven-plugin` in `~/.m2/settings.xml`. The
  server id is `central`
- Run `mvn clean deploy -P deploy`
