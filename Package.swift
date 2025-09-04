// swift-tools-version: 5.9
import PackageDescription

let package = Package(
    name: "Maximilien0405CapacitorAndroidRelaunch",
    platforms: [.iOS(.v14)],
    products: [
        .library(
            name: "Maximilien0405CapacitorAndroidRelaunch",
            targets: ["AndroidRelaunchPlugin"])
    ],
    dependencies: [
        .package(url: "https://github.com/ionic-team/capacitor-swift-pm.git", from: "7.0.0")
    ],
    targets: [
        .target(
            name: "AndroidRelaunchPlugin",
            dependencies: [
                .product(name: "Capacitor", package: "capacitor-swift-pm"),
                .product(name: "Cordova", package: "capacitor-swift-pm")
            ],
            path: "ios/Sources/AndroidRelaunchPlugin"),
        .testTarget(
            name: "AndroidRelaunchPluginTests",
            dependencies: ["AndroidRelaunchPlugin"],
            path: "ios/Tests/AndroidRelaunchPluginTests")
    ]
)
