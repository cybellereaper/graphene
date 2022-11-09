<div align="center"><img src="assets\grap.png" width="50%"/></div>

#### Graphene Lua Scripting Engine

A few events work right now.
Working on improvements!

#### Example Scripts

```lua
function onEnable(plugin)
    plugin:hookEvent("BlockBreakEvent", onBlockBreakEvent)
end
function onBlockBreakEvent(event) 
    player = event:getPlayer()
    player:sendMessage("kaboom!")
end
```

#### Dependencies

![](https://skillicons.dev/icons?i=mongodb&theme=dark)

#### Why?

This project was created for people, who get tired of compiling their code and want to contribute more frequently and faster without having to "mess" with the compiler, where they can just drag-&-drop from each server.

#### Repository is under Mozilla License.
