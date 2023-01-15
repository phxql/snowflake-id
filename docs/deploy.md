- Import GPG signing key
- Configure credentials for `nexus-staging-plugin`,
  see [here](https://help.sonatype.com/repomanager2/staging-releases/configuring-your-project-for-deployment). The
  server id is `ossrh`
- Run `mvn clean deploy -P deploy`
