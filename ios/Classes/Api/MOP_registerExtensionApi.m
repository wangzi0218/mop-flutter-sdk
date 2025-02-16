//
//  MOP_registerExtensionApi.m
//  mop
//
//  Created by 康旭耀 on 2020/4/20.
//

#import "MOP_registerExtensionApi.h"
#import "MopPlugin.h"
#import <FinApplet/FinApplet.h>

@implementation MOP_registerExtensionApi

- (void)setupApiWithSuccess:(void (^)(NSDictionary<NSString *,id> * _Nonnull))success failure:(void (^)(id _Nullable))failure cancel:(void (^)(void))cancel
{
    NSLog(@"MOP_registerExtensionApi");
    FlutterMethodChannel *channel = [[MopPlugin instance] methodChannel];
    [[FATClient sharedClient] registerExtensionApi:self.name handle:^(id param, FATExtensionApiCallback callback) {
        NSLog(@"invoke ExtensionApi:");
        NSLog(@"%@",self.name);
        NSLog(@"%@",param);
        NSString* api = [@"extensionApi:" stringByAppendingString:self.name];
        [channel invokeMethod:api arguments:param result:^(id  _Nullable result) {
            NSLog(@"extensionApi reslut:%@",result);
            // 先判断是否flutter发生错误
//            BOOL isFlutterError = [result isKindOfClass:[FlutterError class]] || result == FlutterMethodNotImplemented;
            BOOL isValid = [result isKindOfClass:[NSDictionary class]];
            if (!isValid) {
                NSLog(@"extensionApi reslut is not NSDictionary");
                callback(FATExtensionCodeFailure,nil);
                return;
            }
            // 再判断回调是否为失败
            BOOL hasError = [[result allKeys] containsObject:@"errMsg"];
            if (hasError) {
                NSString *errMsg = result[@"errMsg"];
                NSString *errPrefix = [NSString stringWithFormat:@"%@:fail", self.name];
                BOOL isFail = [errMsg hasPrefix:errPrefix];
                if (isFail) {
                    NSLog(@"extensionApi reslut:fail");
                    callback(FATExtensionCodeFailure,nil);
                    return;
                }
            }
            // 其他的按成功处理
            NSLog(@"extensionApi callback:%@",result);
            callback(FATExtensionCodeSuccess,result);
        }];
    }];
    success(@{});
}

@end
