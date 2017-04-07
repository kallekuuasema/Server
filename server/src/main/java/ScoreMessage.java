package kalle.server;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;

@XmlAccessorType(XmlAccessType.FIELD)
public class ScoreMessage
{
  public String playerName;
  public String playerId;
  public String gameName;
  public long score;
}