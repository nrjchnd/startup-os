package com.google.startupos.tools.aa.commands;

import com.google.startupos.common.FileUtils;
import com.google.startupos.common.flags.Flag;
import com.google.startupos.common.flags.FlagDesc;
import com.google.startupos.common.flags.Flags;
import java.io.IOException;
import java.nio.file.Paths;
import javax.inject.Inject;

public class WorkspaceCommand implements AaCommand {
  @FlagDesc(
    name = "force",
    description = "Force workspace switching creating workspace if non-existent"
  )
  public static Flag<Boolean> force = Flag.create(false);

  @FlagDesc(name = "ws", description = "Workspace name to switch to", required = true)
  public static Flag<String> ws = Flag.create("");

  private FileUtils fileUtils;
  private ConfigProvider config;

  @Inject
  public WorkspaceCommand(FileUtils utils, ConfigProvider configProvider) {
    this.fileUtils = utils;
    this.config = configProvider;
  }

  @Override
  public void run(String[] args) {
    Flags.parse(args, WorkspaceCommand.class.getPackage());
    String basePath = config.getConfig().getBasePath();

    String user = System.getenv("USER");
    String headPath = Paths.get(basePath, "head").toAbsolutePath().toString();
    String newWsPath = Paths.get(basePath, user, "ws", ws.get()).toAbsolutePath().toString();

    if (force.get()) {
      fileUtils.mkdirs(newWsPath);
      try {
        fileUtils.copyDirectoryToDirectory(headPath, newWsPath);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    System.out.println(String.format("cd %s", newWsPath));
  }

  @Override
  public String getName() {
    return "workspace";
  }
}