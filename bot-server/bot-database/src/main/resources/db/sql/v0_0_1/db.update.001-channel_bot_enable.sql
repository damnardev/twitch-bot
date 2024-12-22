UPDATE t_channel SET bot_enabled = (SELECT tb.enabled FROM t_bot tb WHERE tb.id = t_channel.id);
