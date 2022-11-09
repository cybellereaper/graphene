<div id="Graphene" align="center">
    <img src="assets\grap.png" width="40%"/>
    <h4>Graphene Lua Script</h4>
    <h4>Highly in development stages, some features won't work</h4>
</div>

<h4>Example Event Code</h4>

```lua
function onEnable(plugin)
    plugin:hookEvent("BlockBreakEvent", onBlockBreakEvent)
end
function onBlockBreakEvent(event) 
    player = event:getPlayer()
    player:sendMessage("kaboom!")
end
```
