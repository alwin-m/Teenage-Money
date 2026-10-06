# Offline architecture

Core prototype flows are client-side React state plus localStorage. Once loaded, adding money, logging expenses, computing balances, rendering activity, and showing receipts require no network request. The only external dependency in the current CSS is a Google Fonts import; production offline packaging should bundle a local font or use a system fallback.

