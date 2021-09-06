export interface Server {
  id: number;
  name: string;
  platform: string;
  serverDir: string;
  startFilePath: string;
  serverProperties: ServerProperties;
}

export interface ServerProperties {
  
}

// private final List<UserDto> userDtos = new ArrayList<>();
// private final List<String> players = new LinkedList<>();
// private Path startFilePath;
// private String platform;
// private final ServerProperties serverProperties = new ServerProperties();


