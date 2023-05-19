package project.client.model;

public class PlayerModel {
    String name , hostName;
    Character[] tiles = new Character[7];

    public PlayerModel(String name , String hostName)
    {
        this.name = name;
        this.hostName = hostName;
    }

    public void takeTile()
    {
        for(Character tile : tiles)
        {
            if(tile == null)
            {
                String msgRequestTile = "2,T,".concat(name);
                /*
                 String givenTile = GameModel.requestTile( msgRequestTile );
                 if( tile.length == 1 )
                    tile = givenTile.charAt(0);
                 */
            }
        }
    }

    public Integer getScore()
    {
        String msgRequestScore = "2,P,S,".concat(name);
        /*
        String scorePlayer = GameModel.requestScore( msgScorePlayer ) ;
        return Integer.parseInt(scorePlayer);
        */
    }
    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String getHostName()
    {
        return hostName;
    }

}
