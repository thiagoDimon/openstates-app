import { useState } from 'react'
import { Avatar, Box, Card, CardContent, CardMedia, Chip, Typography } from '@mui/material'
import type { Politician } from '@/types/politician'

interface PoliticianCardProps {
  politician: Politician
}

export function PoliticianCard({ politician }: PoliticianCardProps) {
  const [imgError, setImgError] = useState(false)
  const state = politician.roles[0]?.jurisdictionName ?? '—'

  return (
    <Card sx={{ height: '100%', display: 'flex', flexDirection: 'column' }}>
      {politician.imageUrl && !imgError ? (
        <CardMedia
          component="img"
          height="220"
          image={politician.imageUrl}
          alt={politician.name}
          sx={{ objectFit: 'cover' }}
          onError={() => setImgError(true)}
        />
      ) : (
        <Box sx={{ height: 220, display: 'flex', alignItems: 'center', justifyContent: 'center', bgcolor: 'grey.100' }}>
          <Avatar sx={{ width: 80, height: 80, fontSize: 36 }}>
            {politician.name.charAt(0) || '?'}
          </Avatar>
        </Box>
      )}

      <CardContent sx={{ flexGrow: 1 }}>
        <Typography variant="subtitle1" sx={{ fontWeight: 'bold' }} gutterBottom>
          {politician.name}
        </Typography>
        <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap', mt: 1 }}>
          <Chip label={state} size="small" variant="outlined" />
          <Chip label={politician.party} size="small" color="primary" variant="outlined" />
        </Box>
      </CardContent>
    </Card>
  )
}
