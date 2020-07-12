package DataSource;

import org.kohsuke.args4j.Option;

public class ClientCommand {
    @Option(required = true, name = "-h", usage = "Host name")
    private String host;

    @Option(required = true, name = "-p", usage = "Port number")
    private int port;

    @Option(required = true, name = "-u", usage = "Username")
    private String username;

    public String getHost() { return host; }

    public int getPort() { return port; }

    public String getUsername() { return username; }
}
