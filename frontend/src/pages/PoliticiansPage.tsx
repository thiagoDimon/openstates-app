import { useState } from 'react'
import { Box, CircularProgress, Container, Typography } from '@mui/material'
import { useFilterOptions, usePoliticians } from '@/hooks/usePoliticians'
import { FilterBar } from '@/components/FilterBar'
import { PoliticianGrid } from '@/components/PoliticianGrid'

export function PoliticiansPage() {
  const [state, setState] = useState('')
  const [party, setParty] = useState('')

  const { data: filterOptions, isLoading: filtersLoading, isError: filtersError } = useFilterOptions()
  const { data: politicians = [], isLoading, isError } = usePoliticians(
    state || undefined,
    party || undefined,
  )

  return (
    <Container maxWidth="xl" sx={{ py: 4 }}>
      <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 3 }}>
        US Politicians
      </Typography>

      {filtersLoading ? (
        <Box sx={{ mb: 3 }}>
          <CircularProgress size={24} />
        </Box>
      ) : filtersError ? (
        <Typography color="error" sx={{ mb: 3 }}>
          Failed to load filter options. Please refresh the page.
        </Typography>
      ) : filterOptions ? (
        <FilterBar
          filters={filterOptions}
          state={state}
          party={party}
          onStateChange={setState}
          onPartyChange={setParty}
        />
      ) : null}

      <PoliticianGrid
        politicians={politicians}
        isLoading={isLoading}
        isError={isError}
      />
    </Container>
  )
}
