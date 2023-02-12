<div align="center"><img src="assets\logo.png" width="50%"/></div>

### Graphene Lua Scripting Engine

### Examples of Scripts

```lua
function onEnable(plugin)
    plugin:hookEvent("BlockBreakEvent", onBlockBreakEvent)
end
function onBlockBreakEvent(event) 
    player = event:getPlayer()
    player:sendMessage("kaboom!")
end
```

#### Why?
The goal of this project is to allow people who are tired of compiling their code to contribute more frequently and rapidly without having to "mess" with the compiler by just dragging and dropping their code to each server.

The repository is licensed under the WTFPL V2 Free Software License.
