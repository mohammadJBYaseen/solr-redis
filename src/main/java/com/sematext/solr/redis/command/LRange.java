package com.sematext.solr.redis.command;

import org.apache.solr.common.params.SolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCommands;
import java.util.Map;

public final class LRange implements Command<JedisCommands> {
  private static final Logger log = LoggerFactory.getLogger(LRange.class);

  @Override
  public Map<String, Float> execute(final JedisCommands client, final SolrParams params) {
    final String key = ParamUtil.assertGetStringByName(params, "key");
    final int min = ParamUtil.tryGetIntByName(params, "min", 0);
    final int max = ParamUtil.tryGetIntByName(params, "max", -1);

    log.debug("Fetching LRANGE from Redis for key: {} ({}, {})", key, min, max);

    // Use decrementing Scorer to preserve list ordering
    return ResultUtil.stringIteratorToMap(client.lrange(key, min, max), 0F, new ResultUtil.Scorer() {
      @Override
      public Float score(final Float score) {
        return score - 1F;
      }
    });
  }
}
