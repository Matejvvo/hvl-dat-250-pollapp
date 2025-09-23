# Report

- [GitHub repository](https://github.com/Matejvvo/hvl-dat-250-pollapp/)
- [Assignment](https://github.com/selabhvl/dat250public/blob/master/expassignments/expass5.md)

## Setup

### Install

```bash
# Install on Mac using HomeBrew
brew tap redis/redis
brew install --cask redis
echo $PATH  # Check path
```

### CLI Control

```bash
redis-server $(brew --prefix)/etc/redis.conf  # Start default local server
redis-cli  # Start CLI
ping  # pong
redis-cli SHUTDOWN  # Stop the server
```

### Basic Commands

- Strings: SET, GET, MGET, INCR, DECR
- Lists: LPUSH, RPUSH, LPOP, RPOP, LRANGE
- Sets: SADD, SREM, SMEMBERS, SINTER, SUNION
- Hashes: HSET, HGET, HMGET, HGETALL
- Sorted Sets: ZADD, ZREM, ZRANGE, ZREVRANK, ZINCRBY
- Streams: XADD, XRANGE, XREAD, XGROUP

## Use Case 1

1. Initial state: no user is logged in
    ```redis
     127.0.0.1:6379> SMEMBERS logged_in
     (empty array)
     ```
2. User "alice" logs in
    ```redis
    127.0.0.1:6379> SADD logged_in alice
    (integer) 1
    ```
3. User "bob" logs in
    ```redis
    127.0.0.1:6379> SADD logged_in bob
    (integer) 1
    ```
    ```redis
    127.0.0.1:6379> SMEMBERS logged_in
    1) "alice"
    2) "bob"
    ```
4. User "alice" logs off
    ```redis
    127.0.0.1:6379> SREM logged_in alice
    (integer) 1
    ```
5. User "eve" logs in
    ```redis
    127.0.0.1:6379> SADD logged_in eve
    (integer) 1
    ```
    ```redis
    127.0.0.1:6379> SMEMBERS logged_in
    1) "bob"
    2) "eve"
    ```

## Use Case 2

```redis
127.0.0.1:6379> HSET poll:03ebcb7b-bd69-440b-924e-f5b7d664af7b title "Pineapple on Pizza?" option:0:caption "Yes, yammy!" option:0:voteCount 269 option:1:caption "Mamma mia, nooooo!" option:1:voteCount 268 option:2:caption "I do not really care ..." option:2:voteCount 42
(integer) 7
127.0.0.1:6379> HGETALL poll:03ebcb7b-bd69-440b-924e-f5b7d664af7b
 1) "title"
 2) "Pineapple on Pizza?"
 3) "option:0:caption"
 4) "Yes, yammy!"
 5) "option:0:voteCount"
 6) "269"
 7) "option:1:caption"
 8) "Mamma mia, nooooo!"
 9) "option:1:voteCount"
10) "268"
11) "option:2:caption"
12) "I do not really care ..."
13) "option:2:voteCount"
14) "42"
```

```redis
127.0.0.1:6379> HINCRBY poll:03ebcb7b-bd69-440b-924e-f5b7d664af7b option:0:voteCount 1
(integer) 270
```

## Java & Report



