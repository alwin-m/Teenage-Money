export const positiveAmount = value => {
  const amount = Number(value);
  return Number.isFinite(amount) && amount > 0 ? amount : null;
};

export const allocate = (spaces, destination, amount) => {
  const value = positiveAmount(amount);
  if (!value) throw new Error('Amount must be greater than zero');
  return { ...spaces, [destination]: (spaces[destination] || 0) + value };
};

export const spend = (spaces, space, amount) => {
  const value = positiveAmount(amount);
  if (!value) throw new Error('Amount must be greater than zero');
  if ((spaces[space] || 0) < value) throw new Error('Insufficient balance');
  return { ...spaces, [space]: spaces[space] - value };
};

export const percentage = (amount, total) => total > 0 ? Math.round((amount / total) * 10000) / 100 : 0;

