import api from './axios';

export const getDashboard = () =>
  api.get('/api/dashboard', {
    params: { _t: Date.now() },
    headers: {
      'Cache-Control': 'no-cache',
      Pragma: 'no-cache',
      Expires: '0',
    },
  });
