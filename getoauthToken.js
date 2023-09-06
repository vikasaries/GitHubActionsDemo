const axios = require('axios');

async function generateOAuthToken() {
  try {
    const tokenUrl = 'https://haleon-cpi-dev-7ua1w2tv.authentication.eu20.hana.ondemand.com/oauth/token'; // Replace with the actual token endpoint
    const clientId = process.env.CLIENT_ID; // Set this as a repository secret
    const clientSecret = process.env.CLIENT_SECRET; // Set this as a repository secret
    const grantType = 'client_credentials'; // or other grant type as needed

    const response = await axios.post(tokenUrl, null, {
      // auth: {
      //   username: clientId,
      //   password: clientSecret
      // },
      headers:{
        Authorization: 'Basic c2ItZTMyNzJjNWQtZGVkNy00Yjk3LTk4ZjUtYjJlOTM4M2Q1ZTk4IWI3MjU3fGl0IWIyNTk6ODEwYjc3MDQtZjExNi00MDQzLWJjODctODlhOTYxNDk4NGI2JFJPLUtmOUQ3a2hqclAwMTR4Z1RYQndwYUx5TDlkU2Jmb1V6NUVUZEdtUEE9'
      },
      params: {
        // client_id: clientId,
        // client_secret: clientSecret,
        grant_type: grantType,
      },
    });

    if (response.status === 200) {
      return response.data.access_token;
    } else {
      throw new Error('OAuth token generation failed');
    }
  } catch (error) {
    console.error('Error generating OAuth token:', error.message);
    throw error;
  }
}

generateOAuthToken()
  .then(token => {
    console.log('Generated OAuth Token:', token);
    console.log(`oauthtoken=token" >> $GITHUB_OUTPUT`);
//    console.log(`::set-output name=oauth_token::${token}`);
    
  })
  .catch(error => {
    console.error('OAuth token generation failed:', error.message);
    process.exit(1);
  });
