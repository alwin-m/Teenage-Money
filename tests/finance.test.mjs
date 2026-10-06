import assert from 'node:assert/strict';
import { allocate, percentage, positiveAmount, spend } from '../src/finance.js';

assert.equal(positiveAmount('120'), 120);
assert.equal(positiveAmount('0'), null);
assert.equal(positiveAmount('-4'), null);
assert.deepEqual(allocate({ Spending: 0 }, 'Spending', 120), { Spending: 120 });
assert.deepEqual(spend({ Spending: 120 }, 'Spending', 40), { Spending: 80 });
assert.throws(() => spend({ Spending: 10 }, 'Spending', 11), /Insufficient balance/);
assert.equal(percentage(250, 1000), 25);
assert.equal(percentage(1, 0), 0);
console.log('finance tests passed');

