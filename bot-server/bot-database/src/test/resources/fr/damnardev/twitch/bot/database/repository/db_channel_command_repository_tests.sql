INSERT INTO t_channel(id, name, online, bot_enabled)
VALUES (1, 'channel_01', true, true),
       (2, 'channel_02', true, false);

INSERT INTO t_channel_command(id, name, enabled, channel_id, cooldown, last_execution)
VALUES (1, '!foo', true, 1, 60, null);

INSERT INTO t_channel_command_message(channel_command_id, message)
VALUES (1, 'channel_command_message_01');
