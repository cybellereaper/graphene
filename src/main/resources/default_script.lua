function onEnable(plugin)
    plugin:hookEvent("BlockBreakEvent", onBlockBreakEvent)
end
function onBlockBreakEvent(event)
    player = event:getPlayer()
    player:sendMessage("kaboom!")
end