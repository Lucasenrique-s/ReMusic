package com.Recommendusic;

public class DeezerApi {
    public static void main(String[] args) throws DeezerException {
        DeezerApi deezerApi = new DeezerApi();

        Album album = deezerApi.album().getById(302127).execute();
        System.out.println(album);

        TrackData trackData = deezerApi.search().searchTrack("eminem").execute();
        System.out.println(trackData);
    }
}
