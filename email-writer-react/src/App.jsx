import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import Container from '@mui/material/Container';
import Typography from '@mui/material/Typography';
import TextField from '@mui/material/TextField';
import Box from '@mui/material/Box';
import MenuItem from '@mui/material/MenuItem';
import InputLabel from '@mui/material/InputLabel';
import FormControl from '@mui/material/FormControl';
import Select from '@mui/material/Select';
import Button from '@mui/material/Button';
import CircularProgress from '@mui/material/CircularProgress';
import axios from 'axios';



function App() {
  const [emailContent, setEmailContent]=useState('');
  const [tone, setTone]=useState('');
  const [generatedReply, setGeneratedReply]=useState('');
  const [loading, setLoading]=useState(false);
  const [error,setError]=useState('');
  const handleSubmit = async()=>{
    setLoading(true);
    setError('');
    try{
      const response=await axios.post("http://localhost:8080/api/email/generate",{
        emailContent,
        tone
      });
      setGeneratedReply(typeof response.data==='string'?response.data:JSON.stringify(response.data));
    }catch(error){
      setError('Failed to generate email reply Please try again');
      console.error(error);
    }finally{
      setLoading(false);
    }
  };
  return (
      <Container maxWidth="md" sx={{ py: 8}}>
        <Typography variant='h3' component='h1' gutterBottom>
          Email reply generator
        </Typography>
        <Box sx={{mx:3}}>
          <TextField 
              fullWidth
              multiline
              rows={7}
              variant='outlined'
              label='Original Email Content'
              value={emailContent || ''}
              onChange={(e)=> setEmailContent(e.target.value)}
              sx={{mb:4}}
              />
              <FormControl fullWidth sx={{mb:2}}>
                <InputLabel>Tone (Optional)</InputLabel>
                <Select
                value={tone||''}
                label={"Tone (Optional"}
                onChange={(e)=>setTone(e.target.value)}
                >
                  <MenuItem value="">None</MenuItem>
                  <MenuItem value="professional">Professional</MenuItem>
                  <MenuItem value="casual">Casual</MenuItem>
                  <MenuItem value="friendly">Friendly</MenuItem>
                </Select>
              </FormControl>
              <Button
              variant='contained'
              onClick={handleSubmit}
              disabled={!emailContent|| loading} 
              fullWidth
              >
                {loading?<CircularProgress size={24}/> : "Generate Reply"}
              </Button>
        </Box>
        {error &&(
          <Typography color='error' sx={{mb:2}}>
            {error}
          </Typography>
        )
        }

        {generatedReply &&(
          <Box sx={{mt:3}}>
            <Typography variant='h6' gutterBottom>
              Generated Reply:
            </Typography>
            <TextField
            fullWidth
            multiline
            rows={6}
            variant='outlined'
            value={generatedReply||''}
            inputProps={{readOnly: true}}
            ></TextField>
            <Button
            variant="outlined"
            sx={{mt:2}}
            onClick={()=>navigator.clipboard.writeText(generatedReply)}
            >
              Copt to Clipboard
            </Button>
          </Box>
        )}
      </Container>
  )
}

export default App
