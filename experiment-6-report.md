# Report

- [GitHub repository](https://github.com/Matejvvo/hvl-dat-250-pollapp/)
- [Assignment](https://github.com/selabhvl/dat250public/blob/master/expassignments/expass6.md)
- [Backend](https://github.com/Matejvvo/hvl-dat-250-pollapp/tree/main/backend)

## Setup

```bash
# install on Mac
brew install rabbitmq

# start a local server in background
brew services start rabbitmq

# highly recommended: enable all feature flags on the running node
/opt/homebrew/sbin/rabbitmqctl enable_feature_flag all

# stop the server
brew services stop rabbitmq
```

## Implementation

Configuration `RabbitConfig` has 3 queues: `poll.created.q`, `poll.votes.all.q`, and `vote.cmd.q` and binds them to the `polls.x` exchange. I also implemented a simple template with `Jackson2JsonMessageConverter` for serializing DTOs to message JSON.

Then I created a new service `PollAppEventPublisher` that publishes poll creation, vote cast/update/delete messages. I just simply called them from the respective already existing services, to publish the messages.

Lastly I made a simple listener `VoteEventsListener` to demonstrate listening to the messages. It listens on `vote.cmd.q` for `vote.cmd.cast`. It parses the routing key, the payload, and casts the vote using the already existing service.

## Testing

I tested messages publishing simply by observing `http://localhost:15672/#/queues`. For example:

```bash
Message 1

The server reported 5 messages remaining.

Exchange	            polls.x
Routing Key	            poll.64192550-2da8-4536-a612-3186fa6e6f41.vote.cast
Properties	
    priority:	        0
    delivery_mode:	    2
    headers:	        __TypeId__: no.hvl.dat250.pollapp.service.rabbit.VoteEventDTO
    content_encoding:   UTF-8
    content_type:	    application/json
Payload
    {
        "id":           "e2b5c264-bb7c-4f74-b551-28b8b4022d7b",
        "publishedAt":  1759311406.098510000,
        "voterId":      "62051160-98e7-485e-88a3-0c08e9ee30c7",
        "optionId":     "595258b8-0f9a-40ac-a7a3-7e0013694fa8",
        "pollId":       "64192550-2da8-4536-a612-3186fa6e6f41"
    }
```

To test subscribing I removed the vote in my GUI, sent the following command with the same payload. And a vote appeared in the PollApp GUI, indicating that it works successfully.

```bash
rabbitmqadmin publish exchange=polls.x routing_key=vote.cmd.cast \
  payload='{
    "id":           "e2b5c264-bb7c-4f74-b551-28b8b4022d7b",
    "publishedAt":  1759311406.098510000,
    "voterId":      "62051160-98e7-485e-88a3-0c08e9ee30c7",
    "optionId":     "595258b8-0f9a-40ac-a7a3-7e0013694fa8",
    "pollId":       "64192550-2da8-4536-a612-3186fa6e6f41"
  }' \
  properties='{"content_type": "application/json"}'
  
# Message published
```

---

