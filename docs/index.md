---
layout: home
---

![Title Banner](assets/images/logo-banner.png)

<center>
<img src="https://img.shields.io/badge/dynamic/json?label=spigot%20downloads&query=stats.downloads&url=https%3A%2F%2Fapi.spigotmc.org%2Fsimple%2F0.2%2Findex.php%3Faction%3DgetResource%26id%3D95422" alt="Spigot Downloads"/>
<img src="https://img.shields.io/badge/dynamic/json?label=spigot%20rating&query=stats.rating&url=https%3A%2F%2Fapi.spigotmc.org%2Fsimple%2F0.2%2Findex.php%3Faction%3DgetResource%26id%3D95422" alt="Spigot Rating"/>
<img src="https://img.shields.io/github/release/zhenghanlee/LargeRaids.svg?label=github%20release" alt="GitHub Release">
<img src="https://img.shields.io/discord/846941711741222922.svg?logo=discord" alt="Discord">
<img src="https://img.shields.io/github/license/zhenghanlee/LargeRaids" alt="License"/>
<img src="https://img.shields.io/github/commit-activity/m/zhenghanlee/LargeRaids" alt="Commit Activity">
<img src="https://app.codacy.com/project/badge/Grade/e2b8ef0d41e3404b91a62a35196c7e9e" alt="Codacy Badge">
<img src="https://jitpack.io/v/zhenghanlee/LargeRaids-API.svg" alt="Jitpack">
</center>

**LargeRaids** is a vanilla Spigot game experience enhancement plugin for [raids](https://minecraft.fandom.com/wiki/Raid), which are added to the game in the _Village & Pillage Update_. It expands the raid's mechanism to accommodate for the multiplayer environment with higher difficulty, higher bad omen levels, more raiders, more waves and higher rewards.

<center><h1 style="font-family: Luminari">Server Requirements</h1></center>

The plugin is generally meant for game versions 1.14.x - 1.18.x. More specifically, the supported NMS versions are:

- 1_14_R1 (1.14, 1.14.1, 1.14.2, 1.14.3, 1.14.4)
- 1_15_R1 (1.15, 1.15.1, 1.15.2)
- 1_16_R3 (1.16.4, 1.16.5)
- 1_17_R1 (1.17, 1.17.1)
- 1_18_R1 (1.18, 1.18.1)
- 1_18_R2 (1.18.2)

You may also refer to the corresponding versions [here](https://www.spigotmc.org/wiki/spigot-nms-and-minecraft-versions-1-16/) to check if your server version is supported.

<center><h1 style="font-family: Luminari">Support</h1></center>

Approach our [Discord server](https://discord.gg/YSv7pptDjE) or [Github Issues](https://github.com/zhenghanlee/LargeRaids/issues) for support. This can include bug reporting, usages and suggestions. You are also welcome to put up your suggestions on our [Spigot Discussion page](https://www.spigotmc.org/threads/largeraids-1-14-x-1-18-x.521752/).

<center><h1 style="font-family: Luminari">bStats</h1></center>

[![](https://bstats.org/signatures/bukkit/LargeRaids.svg)](https://bstats.org/plugin/bukkit/LargeRaids/13910)

<center><h1 style="font-family: Luminari">Sponsors</h1></center>

[![StelleNode](https://cdn.discordapp.com/attachments/928421223958970369/928756914345631785/stellanode.gif)](https://stellanode.com/)

Use code **`LARGERAIDS`** at checkout to receive a **15% discount** on the first month for any new StellaNode product.

<center><h1 style="font-family: Luminari">Donation</h1></center>

Thank you for your kind donations! This is an individual project, and your support means a lot to me. If you are donating with Debit/Credit Card, put an identifiable billing name (e.g. Discord username) so that I know who you are. Join our [Discord server](https://discord.gg/YSv7pptDjE) and you will receive a *donor role* on Discord regardless of the amount you donated!

<div id="smart-button-container">
<div style="text-align: center"><label for="amount"> </label><input name="amountInput" type="number" id="amount" value="" ><span> USD</span></div>
    <p id="priceLabelError" style="visibility: hidden; color:red; text-align: center;">Please enter an amount</p>
<div style="text-align: center;" id="paypal-button-container"></div>
</div>
<script src="https://www.paypal.com/sdk/js?client-id=AVmYlcWvLRANOiyAFZ3_NvS-iO8a0rJIGeXZlOLgRxv4DmzkJWjC-f_GSdzT0RH67DmApKCydQ5tfuJO&enable-funding=venmo&currency=USD" data-sdk-integration-source="button-factory"></script>
<script>
function initPayPalButton() {
    var amount = document.querySelector('#smart-button-container #amount');
    var priceError = document.querySelector('#smart-button-container #priceLabelError');
    var elArr = [amount];
    var purchase_units = [];
    purchase_units[0] = {};
    purchase_units[0].amount = {};
    function validate(event) {
        return event.value.length > 0;
    }
    paypal.Buttons({
        style: {
        color: 'gold',
        shape: 'rect',
        label: 'paypal',
        layout: 'vertical',
        },
        onInit: function (data, actions) {
            actions.disable();
            elArr.forEach(function (item) {
                item.addEventListener('keyup', function (event) {
                    var result = elArr.every(validate);
                    if (result) {
                        actions.enable();
                    } else {
                        actions.disable();
                    }
                });
            });
        },
        onClick: function () {
            if (amount.value.length < 1) {
                priceError.style.visibility = "visible";
            } else {
                priceError.style.visibility = "hidden";
            }
            purchase_units[0].description = "LargeRaids Donation";
            purchase_units[0].amount.value = amount.value;
        },
        createOrder: function (data, actions) {
            return actions.order.create({
                purchase_units: purchase_units,
                application_context: {
                    shipping_preference: 'NO_SHIPPING'
                }
            });
        },
        onApprove: function (data, actions) {
            return actions.order.capture().then(function (orderData) {
                // Full available details
                console.log('Capture result', orderData, JSON.stringify(orderData, null, 2));
                // Show a success message within this page, e.g.
                const element = document.getElementById('paypal-button-container');
                element.innerHTML = '';
                element.innerHTML = '<h3>Thank you for your payment!</h3>';
                // Or go to another URL:  actions.redirect('thank_you.html');
            });
        },
        onError: function (err) {
            console.log(err);
        }
    }).render('#paypal-button-container');
}
initPayPalButton();
</script>