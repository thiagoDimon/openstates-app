import { useState } from 'react'
import { Avatar, Box, Card, CardContent, CardMedia, Typography } from '@mui/material'
import GroupsIcon from '@mui/icons-material/Groups'
import LocationOnIcon from '@mui/icons-material/LocationOn'
import type { Politician } from '@/types/politician'

interface PoliticianCardProps {
  politician: Politician
}

export function PoliticianCard({ politician }: PoliticianCardProps) {
  const [imgError, setImgError] = useState(false)
  const state = politician.roles[0]?.jurisdictionName ?? '—'

  return (
    <Card
      sx={{
        height: '100%',
        display: 'flex',
        flexDirection: 'column',
        borderRadius: 4,
        boxShadow: '0 2px 16px rgba(0,0,0,0.08)',
        border: 'none',
      }}
    >
      <Box sx={{ p: 1.5, pb: 0 }}>
        <Box
          sx={{
            borderRadius: 3,
            overflow: 'hidden',
            height: { xs: 360, sm: 340 },
          }}
        >
          {politician.imageUrl && !imgError ? (
            <CardMedia
              component="img"
              height="100%"
              image={politician.imageUrl}
              alt={politician.name}
              sx={{
                objectFit: 'cover',
                objectPosition: 'center 10%',
                height: '100%',
              }}
              onError={() => setImgError(true)}
            />
          ) : (
            <Box
              sx={{
                height: '100%',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                bgcolor: 'grey.100',
              }}
            >
              <Avatar sx={{ width: 80, height: 80, fontSize: 36 }}>{politician.name.charAt(0) || '?'}</Avatar>
            </Box>
          )}
        </Box>
      </Box>

      <CardContent sx={{ pt: 1.5 }}>
        <Typography variant="h6" sx={{ fontWeight: 700, lineHeight: 1.2, mb: 1 }}>
          {politician.name}
        </Typography>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5, mb: 0.5 }}>
          <LocationOnIcon sx={{ fontSize: 16, color: 'text.disabled' }} />
          <Typography variant="body2" color="text.secondary">
            {state}
          </Typography>
        </Box>

        <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
          <GroupsIcon sx={{ fontSize: 16, color: 'text.disabled' }} />
          <Typography variant="body2" color="text.secondary">
            {politician.party}
          </Typography>
        </Box>
      </CardContent>
    </Card>
  )
}
