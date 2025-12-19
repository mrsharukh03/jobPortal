// authApi.js
import axiosInstance from './axiosInstance';

/**
 * Check if user is authenticated
 * @returns {Promise<{ authenticated: boolean, message: string, statusCode: number }>}
 */
export async function checkLoginStatus() {
  try {
    const response = await axiosInstance.get('/auth/check');

    return {
      authenticated: true,
      message: response.data?.message || 'Authenticated',
      statusCode: response.status,
    };
  } catch (error) {
    if (error.response) {
      // Server responded but with error status
      return {
        authenticated: false,
        message: error.response.data?.message || 'Not authenticated',
        statusCode: error.response.status,
      };
    } else {
      // Network error or server down
      return {
        authenticated: false,
        message: 'Server is unreachable. Please try again later.',
        error: error.message,
        statusCode: 0,
      };
    }
  }
}

/**
 * Logs the user out by clearing cookies on the server
 * @returns {Promise<{ success: boolean, message: string, statusCode: number }>}
 */
export async function logout() {
  try {
    const response = await axiosInstance.post('/auth/logout');

    return {
      success: true,
      message: response.data?.message || 'Logout successful',
      statusCode: response.status,
    };
  } catch (error) {
    if (error.response) {
      return {
        success: false,
        message: error.response.data?.message || 'Logout failed',
        statusCode: error.response.status,
      };
    } else {
      return {
        success: false,
        message: 'Server is unreachable. Please try again later.',
        error: error.message,
        statusCode: 0,
      };
    }
  }
}
