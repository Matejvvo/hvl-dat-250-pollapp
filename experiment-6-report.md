## Setup
```bash
# install
brew install rabbitmq

# start a local server in background
brew services start rabbitmq

# highly recommended: enable all feature flags on the running node
/opt/homebrew/sbin/rabbitmqctl enable_feature_flag all

# stop the server
brew services stop rabbitmq
```
