package com.webforjexplorer;

import com.webforj.App;
import com.webforj.annotation.AppProfile;
import com.webforj.annotation.Routify;
import com.webforj.annotation.StyleSheet;

@Routify(packages = "com.webforjexplorer.views")
@StyleSheet("ws://app.css")
@AppProfile(name = "webforJ Explorer", shortName = "webforJ Explorer")
public class Application extends App {
}
