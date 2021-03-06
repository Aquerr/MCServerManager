package pl.bartlomiejstepien.mcsm.config;

public final class CurseForgeAPIRoutes
{
    public static final String MODPACK_FILES = "https://addons-ecs.forgesvc.net/api/v2/addon/{modpackId}/files";
    public static final String MODPACKS_SEARCH = "https://addons-ecs.forgesvc.net/api/v2/addon/search?categoryId={categoryId}&gameId=432&gameVersion={version}&index={index}&pageSize={size}&searchFilter={modpackName}&sectionId=4471&sort=0";

    public static final String MODPACK_INFO = "https://addons-ecs.forgesvc.net/api/v2/addon/{modpackId}";
    public static final String MODPACK_DESCRIPTION = "https://addons-ecs.forgesvc.net/api/v2/addon/{modpackId}/description";

    public static final String MODPACK_LATEST_SERVER_DOWNLOAD_URL = "https://addons-ecs.forgesvc.net/api/v2/addon/{modpackId}/file/{fileId}/download-url";

    private CurseForgeAPIRoutes()
    {

    }
}
