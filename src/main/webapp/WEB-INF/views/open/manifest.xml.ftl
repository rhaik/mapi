<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
    <dict>
        <key>items</key>
        <array>
            <dict>
                <key>assets</key>
                <array>
                    <dict>
                        <key>kind</key>
                        <string>software-package</string>
                        <key>url</key>
                        <string>${update.download_url}</string>
                    </dict>
                    <dict>
                        <key>kind</key>
                        <string>display-image</string>
                        <key>needs-shine</key>
                        <true/>
                        <key>url</key>
                        <string>http://cdn.erbicun.cn/app-logo.png</string>
                    </dict>
                </array>
                <key>metadata</key>
                <dict>
                    <key>bundle-identifier</key>
                    <string>${update.bundle_id}<#if isIOS8>.ios8fix</#if></string>
                    <key>bundle-version</key>
                    <string>${update.version}</string>
                    <key>kind</key>
                    <string>software</string>
                    <key>title</key>
                    <string>${update.app_name}</string>
                </dict>
            </dict>
        </array>
    </dict>
</plist>