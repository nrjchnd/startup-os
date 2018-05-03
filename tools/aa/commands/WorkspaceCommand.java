package com.google.startupos.tools.aa.commands;

import com.google.startupos.common.FileUtils;
import com.google.startupos.common.flags.Flag;
import com.google.startupos.common.flags.FlagDesc;
import com.google.startupos.common.flags.Flags;
import com.google.startupos.tools.aa.Protos.Config;

import java.io.IOException;
import java.nio.file.Paths;
import javax.inject.Inject;

public class WorkspaceCommand implements AaCommand {
  @FlagDesc(
    name = "force",
    description = "Create workspace if it doesn't exist"
  )
  public static Flag<Boolean> force = Flag.create(false);

  @FlagDesc(name = "ws", description = "Name of workspace to switch to", required = true)
  public static Flag<String> ws = Flag.create("");

  private FileUtils fileUtils;
  private Config config;

  @Inject
  public WorkspaceCommand(FileUtils utils, Config config) {
    this.fileUtils = utils;
    this.config = config;
  }

  @Override
  public void run(String[] args) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-f")) {
        args[i] = "--force";
      }
    }
    Flags.parse(args, WorkspaceCommand.class.getPackage());
    String basePath = config.getBasePath();

    String headPath = Paths.get(basePath, "head").toAbsolutePath().toString();
    String newWsPath = Paths.get(basePath, config.getUser(), "ws", ws.get()).toAbsolutePath().toString();

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
