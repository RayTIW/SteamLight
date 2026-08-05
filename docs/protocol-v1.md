\# SteamLight Protocol v1



Protocol Version



Current: 1



Compatible since:

Firmware 0.3.0

Java Client 0.3.0





Version: 1



## Transport

- USB CDC Serial
- 115200 Baud
- 8N1
- UTF-8
- one JSON object per line
- LF (\n) line termination



---



## General Rules



- UTF-8

- one JSON object per line

- every request receives exactly one response

- commands are case-sensitive

- unknown commands return error

- unknown JSON fields are ignored

- responses are sent immediately

- firmware never sends unsolicited Events


## Message Types

- Commands
{"cmd":"status"}

- Events
{"event":"status"}

- Results
{"result":"ok"}


## Handshake



### hello



Request



```json

{"cmd":"hello"}

```



Response



```json

{

"event":"ready",

"device":"SteamLight",

"version":"0.3.0",

"protocol":1,

"leds":28

}

```



## Commands



### status



Request



```json

{"cmd":"status"}

```



Response



```json

{

"event":"status",

"brightness":25,

"leds":28

}

```



### brightness



Request



```json


{

 "cmd":"brightness",

 "value":25

}

```



Response



```json

{

 "result":"ok",

 "message":"brightness_changed"

}

```



### Effect



### boot



Request



```json

{

 "cmd":"effect",

 "value":"boot"

}

```





Success



```json

{

 "result":"ok",

 "message":"effect_changed"

}

```


### idle

Request

```json
{
  "cmd":"effect",
  "value":"idle"
}
```

Response

```json
{
  "result":"ok",
  "message":"effect_changed"
}
```



### off



Request



```json
{
  "cmd":"effect",
  "value":"off"
}
```

Response

```json
{
  "result":"ok",
  "message":"effect_changed"
}
```


## Error Codes

| Code | Bedeutung |
|------|-----------|
| missing_cmd | cmd fehlt |
| unknown_command | unbekannter Befehl |
| missing_effect | value fehlt |
| unknown_effect | unbekannter Effekt |
| invalid_json | JSON konnte nicht gelesen werden |

# missing_cmd



```json

{

 "result":"error",

 "message":"missing_cmd"

}
```



# unknown_effect



```json


{

 "result":"error",

 "message":"unknown_effect"

}
```

