{
<#if game??>
    "agreement": "${game.agreement!''}",
     "bundle_id": "${game.bundle_id!''}",
    "category": "${game.category!''}",
    "description": "${game.description!''}",
    "download_size": "${game.download_size}",
    "id": "${game.encodedId!''}",
    "icon": "${game.icon!''}",
    "name": "${game.name!''}",
    "order_num": ${game.order_num!0},
    "pay": ${game.pay},
     "platform": ${game.platform!0},
    "player_num": ${game.player_num!0},
    "process_name": "${game.process_name!0}",
    "title": "${game.title!''}",
    "url": "${game.url!''}",
    "version": "${game.version!''}"
    </#if>
}