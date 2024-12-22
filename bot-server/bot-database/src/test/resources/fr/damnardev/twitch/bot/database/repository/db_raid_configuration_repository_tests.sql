INSERT INTO t_channel(id, name, online, bot_enabled)
VALUES (1, 'channel_01', true, true),
       (2, 'channel_02', true, false);

INSERT INTO t_raid_configuration(id, raid_message_enabled, twitch_shoutout_enabled,
                                 wizebot_shoutout_enabled)
VALUES (1, true, true, true);

INSERT INTO t_raid_configuration_message(raid_configuration_id, message)
VALUES (1, 'raid_message_01');
