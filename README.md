<div align="center"><img src="assets\grap.png" width="40%"/></div>

#### Graphene Lua Scripting Engine 

Highly in development stages and some features probably won't work.

#### Event Example (some events don't work yet)

```lua
function onEnable(plugin)
    plugin:hookEvent("BlockBreakEvent", onBlockBreakEvent)
end
function onBlockBreakEvent(event) 
    player = event:getPlayer()
    player:sendMessage("kaboom!")
end
```

#### Project requires:
- MongoDB