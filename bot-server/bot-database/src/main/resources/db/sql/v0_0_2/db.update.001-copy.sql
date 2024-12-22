INSERT INTO t_raid_configuration(id, raid_message_enabled, twitch_shoutout_enabled,
                                 wizebot_shoutout_enabled)
SELECT id, raid_message_enabled, twitch_shoutout_enabled, wizebot_shoutout_enabled
FROM t_channel_raid;


INSERT INTO t_raid_configuration_message(raid_configuration_id, message)
SELECT channel_raid_id, message
FROM t_channel_raid_message;
