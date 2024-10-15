UPDATE t_channel
SET bot_enabled = tb.enabled FROM
    t_bot tb
WHERE t_channel.id = tb.id;