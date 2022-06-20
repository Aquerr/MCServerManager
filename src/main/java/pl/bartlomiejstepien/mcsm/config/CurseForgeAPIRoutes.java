package pl.bartlomiejstepien.mcsm.config;

public final class CurseForgeAPIRoutes
{
    public static final String API_BASE_URL = "https://api.curseforge.com";

    public static final String MODPACKS_SEARCH = API_BASE_URL + "/v1/mods/search?gameId=432&classId=4471&categoryId={categoryId}&gameVersion={version}&index={index}&pageSize={size}&searchFilter={modpackName}&sort=0";

    public static final String MODPACK_FILE = API_BASE_URL + "/v1/mods/{modpackId}/files/{fileId}";
    public static final String MODPACK_FILES = API_BASE_URL + "/v1/mods/{modpackId}/files";

    public static final String MODPACK_INFO = API_BASE_URL + "/v1/mods/{modpackId}";
    public static final String MODPACK_DESCRIPTION = API_BASE_URL + "/v1/mods/{modpackId}/description";

    public static final String MODPACK_LATEST_SERVER_DOWNLOAD_URL = API_BASE_URL + "/v1/addon/{modpackId}/files/{fileId}/download-url";

    private CurseForgeAPIRoutes()
    {

    }
}
